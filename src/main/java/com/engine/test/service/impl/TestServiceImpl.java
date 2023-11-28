package com.engine.test.service.impl;

import com.engine.sql.service.ISqlService;
import com.engine.test.service.ITestService;
import com.engine.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class TestServiceImpl implements ITestService {

    @Resource
    private ISqlService sqlService;

    @Override
    public R query() {
        HashMap<String, Object> queryMap = new HashMap<>();
        queryMap.put("sql", "SELECT * FROM table1 t ");
        HashMap<String, Object> map = new HashMap<>();
        map.put("list-1", sqlService.select(queryMap));
        map.put("list-3", sqlService.select(queryMap));
        return R.success().data(map);
    }
}
