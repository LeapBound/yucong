package com.github.leapbound.yc.camunda;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class YcCamundaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YcCamundaApplication.class, args);
    }

}
