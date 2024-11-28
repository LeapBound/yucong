package com.github.leapbound.yc.action.config;

import com.github.leapbound.yc.action.utils.mybatis.EasySqlInjector;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yamath
 * @date 2023/7/10 13:11
 */
@Configuration
@MapperScan("com.github.leapbound.yc.action.mapper")
public class MybatisPlusConfig {

    @Bean
    public EasySqlInjector sqlInjector() {
        return new EasySqlInjector();
    }
}
