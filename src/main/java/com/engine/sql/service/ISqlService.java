package com.engine.sql.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ISqlService {
    List<LinkedHashMap<String, Object>> select(Map<String, Object> map);

    int insert(String sql);

    int update(String sql);
}
