package com.github.leapbound.yc.action.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

/**
 * @author yamath
 * @since 2023/8/9 13:18
 */
@Component
public class GutsHubServiceClient {

    @Value("${alpha.baseUrl}")
    private String baseUrl;

    @Bean
    public GutsHubService gutsHubService() {
        WebClient gutsHubClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(gutsHubClient))
                        .blockTimeout(Duration.ofSeconds(10))
                        .build();
        return httpServiceProxyFactory.createClient(GutsHubService.class);
    }
}
