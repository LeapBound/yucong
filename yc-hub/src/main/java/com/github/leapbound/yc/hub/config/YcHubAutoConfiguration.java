package com.github.leapbound.yc.hub.config;

import com.github.leapbound.yc.hub.external.HubInteractiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Fred
 * @date 2024/2/22 10:40
 */
@Slf4j
@ComponentScan(basePackages = {"com.github.leapbound.yc.hub"})
public class YcHubAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HubInteractiveService hubInteractiveService() {
        return singleChatDto -> log.warn("HubInteractiveService not found, chat: {}", singleChatDto);
    }
}
