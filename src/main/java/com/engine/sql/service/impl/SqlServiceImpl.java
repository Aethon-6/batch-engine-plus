package com.engine.sql.service.impl;

import com.engine.aop.annotation.DS;
import com.engine.enums.DSTypeEnum;
import com.engine.sql.mapper.SqlMapper;
import com.engine.sql.service.ISqlService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SqlServiceImpl implements ISqlService {
    @Resource
    private SqlMapper sqlMapper;

    @DS
    @Override
    public List<LinkedHashMap<String, Object>> select(Map<String, Object> map) {
        return sqlMapper.select(map);
    }

    @Override
    public int insert(String sql) {
        return sqlMapper.insert(sql);
    }

    @Override
    public int update(String sql) {
        return sqlMapper.update(sql);
    }
}
