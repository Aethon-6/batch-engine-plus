package com.engine.sql.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface SqlMapper {
    @Select("${sql}")
    List<LinkedHashMap<String, Object>> select(Map<String, Object> map);

    @Insert("${sql}")
    int insert(String sql);

    @Update("${sql}")
    int update(String sql);
}
