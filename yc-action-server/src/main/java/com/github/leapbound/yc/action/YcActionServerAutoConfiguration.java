package com.github.leapbound.yc.action;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yamath
 * @since 2024/2/21 16:41
 */
@AutoConfigureBefore(RedisAutoConfiguration.class)
@ComponentScan(basePackages = {"com.github.leapbound.yc.action"})
@MapperScan("com.github.leapbound.yc.action.mapper")
public class YcActionServerAutoConfiguration {
}
