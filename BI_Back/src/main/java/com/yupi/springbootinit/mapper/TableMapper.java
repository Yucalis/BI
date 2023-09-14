package com.yupi.springbootinit.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableMapper {

    void create(@Param("tableName") String tableName, @Param("columnNameList") List<String> columnNameList);

    void insert(@Param("tableName") String tableName, @Param("valueLists") List<List<String>> valueLists);


    List<Map<String, String>> selectAll(@Param("tableName") String tableName);

}
