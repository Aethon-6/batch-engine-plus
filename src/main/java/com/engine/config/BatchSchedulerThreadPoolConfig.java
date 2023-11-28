package com.engine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class BatchSchedulerThreadPoolConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        LOGGER.info("定时任务调度线程池创建开始！");

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        taskScheduler.setThreadNamePrefix("task-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(0);

        LOGGER.info("定时任务调度线程池创建结束！");

        return taskScheduler;
    }
}
