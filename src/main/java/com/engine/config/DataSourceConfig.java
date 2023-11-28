package com.engine.config;

import com.engine.enums.DSTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置类
 */
@Configuration
public class DataSourceConfig {
    /**
     * 创建数据源master
     *
     * @return 数据源master
     */
    @Bean(name = "master")
    @ConfigurationProperties(prefix = "spring.dynamic.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建数据源slave
     *
     * @return 数据源slave
     */
    @Bean(name = "slave")
    @ConfigurationProperties(prefix = "spring.dynamic.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 数据源配置
     *
     * @return 动态数据源切换对象。
     * @Description @Primary赋予该类型bean更高的优先级，使至少有一个bean有资格作为autowire的候选者。
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        Map<Object, Object> dsMap = new HashMap<>(2);
        dsMap.put(DSTypeEnum.MASTER, masterDataSource());
        dsMap.put(DSTypeEnum.SLAVE, slaveDataSource());
        return new DynamicDataSource(masterDataSource(), dsMap);
    }
}
