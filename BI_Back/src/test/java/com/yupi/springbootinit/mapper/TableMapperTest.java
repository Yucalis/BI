package com.yupi.springbootinit.mapper;

import com.yupi.springbootinit.utils.ExcelUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TableMapperTest {

    @Resource
    private TableMapper tableMapper;

    @Test
    void createTest() {
        String tableName = "test_table";
        List<String> columnNameList = new ArrayList<>();
        columnNameList.add("日期");
        columnNameList.add("用户数");
        tableMapper.create(tableName, columnNameList);
    }

    @Test
    void insertTest() {
        String tableName = "test_table";
        List<List<String>> valueLists = new ArrayList<>();
        List<String> valueList1 = new ArrayList<>();
        valueList1.add("1号");
        valueList1.add("10");
        List<String> valueList2 = new ArrayList<>();
        valueList2.add("2号");
        valueList2.add("20");
        valueLists.add(valueList1);
        valueLists.add(valueList2);
        tableMapper.insert(tableName, valueLists);
    }

    @Test
    void selectTest() {
        String talleName = "data_1694697691635";
        List<Map<String, String>> list = tableMapper.selectAll(talleName);
        String csvData = ExcelUtils.excelMapToCsv(list);
        System.out.println(csvData);
    }

}