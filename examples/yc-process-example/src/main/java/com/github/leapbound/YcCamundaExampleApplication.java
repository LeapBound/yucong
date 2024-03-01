package com.github.leapbound;

import com.github.leapbound.yc.camunda.YcCamundaApplication;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author tangxu
 * @since 2024/3/1 15:31
 */
@SpringBootApplication
@EnableProcessApplication
public class YcCamundaExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(YcCamundaApplication.class, args);
    }
}