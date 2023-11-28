package com.engine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class BatchThreadPoolConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        LOGGER.info("线程池创建开始！");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        LOGGER.info("线程池创建结束！");
        return threadPoolExecutor;
    }
}
