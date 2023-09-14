package com.yupi.springbootinit.utils;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yupi.springbootinit.mapper.TableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExcelUtils {

    public static List<List<String>> excelToList(MultipartFile multipartFile) {
        List<List<String>> res = new ArrayList<>();
        List<Map<Integer, String>> list = null;
        try {
            // 读取用户上传的文件excel文件并封装到List集合中
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (CollectionUtils.isEmpty(list)) {
            return res;
        }

        StringBuilder stringBuilder = new StringBuilder();

        // 处理表头
        LinkedHashMap<Integer, String> tableHeadMap = (LinkedHashMap) list.get(0);
        // 过滤掉为 null 的 表头信息
        List<String> headList = tableHeadMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());

        res.add(headList);

        // 处理表单数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> tableBodyMap = (LinkedHashMap) list.get(i);
            // 过滤掉为 null 的表单数据
            List<String> bodyList = tableBodyMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            res.add(bodyList);
        }
        return res;
    }

    public static String excelListToCsv(List<List<String>> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (List<String> list : data) {
            stringBuilder.append(StringUtils.join(list, ",")).append("\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 将excel文件转换为csv文件并输出
     * @param multipartFile 要转换的excel文件
     * @return 转换好后的csv文件（以字符串的形式返回）
     */
    public static String excelToCsv(MultipartFile multipartFile) {
        List<Map<Integer, String>> list = null;
        try {
            // 读取用户上传的文件excel文件并封装到List集合中
             list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (CollectionUtils.isEmpty(list)) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        // 处理表头
        LinkedHashMap<Integer, String> tableHeadMap = (LinkedHashMap) list.get(0);
        // 过滤掉为 null 的 表头信息
        List<String> headList = tableHeadMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());

//        String tableName = "data_" + System.currentTimeMillis();



        stringBuilder.append(StringUtils.join(headList, ",")).append("\n");

        // 处理表单数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> tableBodyMap = (LinkedHashMap) list.get(i);
            // 过滤掉为 null 的表单数据
            List<String> bodyList = tableBodyMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(bodyList, ",")).append("\n");
        }

        return stringBuilder.toString();
    }

    public static String excelMapToCsv(List<Map<String, String>> dataLists) {
        if (CollectionUtils.isEmpty(dataLists)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> headSets = dataLists.get(0).keySet();
        stringBuilder.append(StringUtils.join(headSets, ",")).append("\n");
        for (Map<String, String> dataMap : dataLists) {
            Collection<String> values = dataMap.values();
            stringBuilder.append(StringUtils.join(values, ",")).append("\n");
        }

        return stringBuilder.toString();
    }

//    public static void main(String[] args) {
//        System.out.println(excelToCsv(null));
//    }

}
