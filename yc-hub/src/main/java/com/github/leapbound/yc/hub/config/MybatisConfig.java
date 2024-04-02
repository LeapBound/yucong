package com.github.leapbound.yc.hub.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("com.github.leapbound.yc.hub.mapper")
public class MybatisConfig {
}
