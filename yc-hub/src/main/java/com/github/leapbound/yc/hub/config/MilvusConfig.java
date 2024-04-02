package com.github.leapbound.yc.hub.config;

import io.milvus.client.MilvusServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MilvusConfig {

    //    @Value("${milvus.host}")
    private String host;
    //    @Value("${milvus.port}")
    private Integer port;

    @Bean
    public MilvusServiceClient milvusServiceClient() {
//        ConnectParam connectParam = ConnectParam.newBuilder()
//                .withHost(this.host)
//                .withPort(this.port)
//                .build();
//        return new MilvusServiceClient(connectParam);
        return null;
    }
}
