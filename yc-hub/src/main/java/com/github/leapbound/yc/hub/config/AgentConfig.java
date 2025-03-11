package com.github.leapbound.yc.hub.config;

import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Fred Gu
 * @date 2024-12-04 12:03
 */
@Configuration
public class AgentConfig {

    @Value("${yucong.llm.doubao.apiKey}")
    private String doubaoApiKey;

    @Bean
    public ArkService arkService() {
        return ArkService.builder().apiKey(this.doubaoApiKey).build();
    }
}
