package com.yupi.springbootinit.service.impl;


import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AIManager;
import com.yupi.springbootinit.manager.RedissonManager;
import com.yupi.springbootinit.mapper.TableMapper;
import com.yupi.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yupi.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.BiResponse;
import com.yupi.springbootinit.mq.MQProductor;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.utils.ExcelUtils;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.TextHorizontalOverflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
* @author 叶孙勇
* @description 针对表【chart(图表信息)】的数据库操作Service实现
* @createDate 2023-08-28 15:53:16
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    @Resource
    private AIManager aiManager;

    @Resource
    private RedissonManager redissonManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private MQProductor mqProductor;

    @Resource
    private TableMapper tableMapper;



    @Override
    public BiResponse genChart(MultipartFile multipartFile,
                               GenChartByAiRequest genChartByAiRequest,
                               User loginUser) {
        ThrowUtils.throwIf(!redissonManager.doRateLimit("genChart_" + loginUser.getId()), ErrorCode.FORBIDDEN_ERROR, "请求过于频繁");

        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        //校验文件格式和大小
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 10224L;
        ThrowUtils.throwIf(size > ONE_MB * 10, ErrorCode.PARAMS_ERROR, "文件大小超过10MB");
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xls", "xlsx", "doc", "docx", "txt");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件格式错误");

//        String csvData = ExcelUtils.excelToCsv(multipartFile);
        List<List<String>> dataLists = ExcelUtils.excelToList(multipartFile);
        String csvData = ExcelUtils.excelListToCsv(dataLists);

        //拼接用户的请求
        StringBuilder userRequest = new StringBuilder();
        userRequest.append("分析需求：").append("\n");
        userRequest.append(goal);
        if (StringUtils.isNotBlank(chartType)) {
            userRequest.append(",请使用").append(chartType);
        }
        userRequest.append("\n");
        userRequest.append("原始数据：").append("\n").append(csvData);

        //模型id
//        long modelId = 1659171950288818178L;
        long modelId = 1696869691743600641L;

        String result = aiManager.doChart(modelId, userRequest.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成结果异常");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        String tableName = "data_" + System.currentTimeMillis();
        tableMapper.create(tableName, dataLists.get(0));
        dataLists.remove(0);
        tableMapper.insert(tableName, dataLists);


        //保存生成的图表和结论
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartDate(tableName);
        chart.setCharType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setStatus("success");
        boolean saveResult = this.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());

        return biResponse;
    }

    @Override
    public BiResponse genChartAsync(MultipartFile multipartFile,
                                    GenChartByAiRequest genChartByAiRequest,
                                    User loginUser) {

        ThrowUtils.throwIf(!redissonManager.doRateLimit("genChart_" + loginUser.getId()), ErrorCode.FORBIDDEN_ERROR, "请求过于频繁");

        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        //校验文件格式和大小
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 10224L;
        ThrowUtils.throwIf(size > ONE_MB * 10, ErrorCode.PARAMS_ERROR, "文件大小超过10MB");
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xls", "xlsx", "doc", "docx", "txt");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件格式错误");

//        String csvData = ExcelUtils.excelToCsv(multipartFile);
        List<List<String>> dataLists = ExcelUtils.excelToList(multipartFile);
        String csvData = ExcelUtils.excelListToCsv(dataLists);
        String tableName = "data_" + System.currentTimeMillis();
        tableMapper.create(tableName, dataLists.get(0));
        dataLists.remove(0);
        tableMapper.insert(tableName, dataLists);

        //保存生成的图表和结论
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartDate(tableName);
        chart.setCharType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setStatus("wait");
        boolean saveResult = this.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        mqProductor.sendMessage(chart.getId().toString());

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return biResponse;
    }

    /**
     * 根据用户输入让AI生成图表和分析结论
     * @param multipartFile
     * @param genChartByAiRequest
     * @return
     */
    @Override
    public BiResponse genChartAsyncByTP(MultipartFile multipartFile,
                               GenChartByAiRequest genChartByAiRequest,
                               User loginUser) {

        ThrowUtils.throwIf(!redissonManager.doRateLimit("genChart_" + loginUser.getId()), ErrorCode.FORBIDDEN_ERROR, "请求过于频繁");

        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        //校验文件格式和大小
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 10224L;
        ThrowUtils.throwIf(size > ONE_MB * 10, ErrorCode.PARAMS_ERROR, "文件大小超过10MB");
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xls", "xlsx", "doc", "docx", "txt");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件格式错误");

        String csvData = ExcelUtils.excelToCsv(multipartFile);

        //拼接用户的请求
        StringBuilder userRequest = new StringBuilder();
        userRequest.append("分析需求：").append("\n");
        if (StringUtils.isNotBlank(goal)) {
            userRequest.append(",请使用").append(chartType);
        }
        userRequest.append(goal).append("\n");
        userRequest.append("原始数据：").append("\n").append(csvData);

        //保存生成的图表和结论
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartDate(csvData);
        chart.setCharType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setStatus("wait");
        boolean saveResult = this.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        //模型id
//        long modelId = 1659171950288818178L;
        long modelId = 1696869691743600641L;

        CompletableFuture.runAsync(() -> {
            Chart updateParams = new Chart();
            updateParams.setStatus("running");
            updateParams.setId(chart.getId());
            boolean success = this.updateById(updateParams);
            if (!success) {
                setFailed(chart.getId(), "图表状态更新失败");
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "图表状态更新失败");
            }
            String result = aiManager.doChart(modelId, userRequest.toString());
            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
                setFailed(chart.getId(), "AI 生成结果异常");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成结果异常");
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();
            updateParams = new Chart();
            updateParams.setId(chart.getId());
            updateParams.setGenChart(genChart);
            updateParams.setGenResult(genResult);
            updateParams.setStatus("success");
            success = this.updateById(updateParams);
            if (!success) {
                setFailed(chart.getId(), "图表状态更新失败");
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "图表状态更新失败");
            }
        }, threadPoolExecutor);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return biResponse;
    }

    private void setFailed(Long id, String message) {
        Chart errorParam = new Chart();
        errorParam.setId(id);
        errorParam.setStatus("failed");
        errorParam.setExecMessage(message);
        this.updateById(errorParam);
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
        Long id = chartQueryRequest.getId();
        Long userId = chartQueryRequest.getUserId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String charType = chartQueryRequest.getCharType();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(charType), "charType", charType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.like(StringUtils.isNotEmpty(name), "name", name);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}




