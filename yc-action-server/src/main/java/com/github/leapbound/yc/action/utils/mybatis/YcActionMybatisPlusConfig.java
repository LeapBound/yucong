package com.github.leapbound.yc.action.utils.mybatis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yamath
 * @since 2023/7/10 13:11
 */
@Configuration
public class YcActionMybatisPlusConfig {

    @Bean
    public EasySqlInjector sqlInjector() {
        return new EasySqlInjector();
    }
}
