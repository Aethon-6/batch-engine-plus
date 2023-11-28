package com.engine.batch.controller;

import com.engine.batch.service.IBatchService;
import com.engine.core.execute.common.service.ICommonService;
import com.engine.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("batch")
public class BatchController {
    @Resource
    private IBatchService batchService;
    @Resource
    private ICommonService commonService;

    @PostMapping("init")
    public R init() {
        return batchService.init();
    }

    @PostMapping("execute")
    public R execute() {
        return batchService.execute();
    }

    @GetMapping("generator")
    public R generator() {
        return R.success();
    }
}
