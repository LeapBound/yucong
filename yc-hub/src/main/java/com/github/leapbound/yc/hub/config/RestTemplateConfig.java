package com.github.leapbound.yc.hub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Value("${yucong.action.rest.url}")
    private String yucongActionUrl;
    @Value("${yucong.process.rest.url}")
    private String yucongProcessUrl;

    @Bean
    public RestTemplate actionRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(this.yucongActionUrl)
                .setConnectTimeout(Duration.ofSeconds(1))
                .build();
    }

    @Bean
    public RestTemplate processRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(this.yucongProcessUrl)
                .setConnectTimeout(Duration.ofSeconds(1))
                .build();
    }
}
