package com.engine.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.engine.sql.mapper", "com.engine.active.mapper"})
public class MybatisConfiguration {
}
