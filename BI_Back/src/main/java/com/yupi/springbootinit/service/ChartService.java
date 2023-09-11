package com.yupi.springbootinit.service;

import co.elastic.clients.elasticsearch.sql.QueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yupi.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.BiResponse;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 叶孙勇
* @description 针对表【chart(图表信息)】的数据库操作Service
* @createDate 2023-08-28 15:53:16
*/
public interface ChartService extends IService<Chart> {

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

    BiResponse genChart(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, User loginUser);

    BiResponse genChartAsync(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, User loginUser);

    BiResponse genChartAsyncByTP(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, User loginUser);
}
