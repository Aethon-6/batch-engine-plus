package com.engine.batch.controller;

import com.engine.batch.service.IBatchService;
import com.engine.utils.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("batch")
public class BatchController {
    private IBatchService batchService;

    @PostMapping("init")
    public R init() {
        return batchService.init();
    }

    @PostMapping("execute")
    public R execute() {
        return batchService.execute();
    }
}
