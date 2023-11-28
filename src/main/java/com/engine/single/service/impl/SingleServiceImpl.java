package com.engine.single.service.impl;

import com.alibaba.fastjson.JSON;
import com.engine.entity.vo.ActiveVO;
import com.engine.single.service.ISingleService;
import com.engine.utils.FileUtil;
import com.engine.utils.R;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class SingleServiceImpl implements ISingleService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public R execute(String nodeId) {
        String activeStr = null;
        try {
            activeStr = FileUtil.readFileContent("active.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ActiveVO active = JSON.parseObject(activeStr, ActiveVO.class);

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        assert active != null;
        ops.set("nodes", active.getNodes());

        return R.success();
    }
}
