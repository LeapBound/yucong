package yzggy.yucong.action;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yamath
 * @since 2024/2/21 16:41
 */
@AutoConfigureBefore(RedisAutoConfiguration.class)
@ComponentScan(basePackages = {"yzggy.yucong.action"})
@MapperScan("yzggy.yucong.action.mapper")
public class YcActionServerAutoConfiguration {
}
