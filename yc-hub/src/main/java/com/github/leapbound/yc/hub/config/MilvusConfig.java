package com.github.leapbound.yc.hub.config;

import io.milvus.client.MilvusServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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