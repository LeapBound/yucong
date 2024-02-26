package com.github.leapbound.yc.hub.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Fred
 * @since 2024/2/22 10:40
 */
@ComponentScan(basePackages = {"com.github.leapbound.yc.hub"})
public class YcHubServerAutoConfiguration {
}
