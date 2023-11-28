package com.engine.config;

import com.engine.batch.service.IBatchService;
import com.engine.core.execute.common.service.ICommonService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BatchTaskRunner implements ApplicationRunner {

    @Resource
    private IBatchService batchService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO Auto-generated method stub
        /*batchService.init();
        batchService.execute();*/
    }
}
