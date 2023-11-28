package com.engine.test.controller;

import com.engine.test.service.ITestService;
import com.engine.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("test")
public class TestController {
    @Resource
    private ITestService testService;

    @GetMapping("query")
    public R query() {
        return testService.query();
    }
}
