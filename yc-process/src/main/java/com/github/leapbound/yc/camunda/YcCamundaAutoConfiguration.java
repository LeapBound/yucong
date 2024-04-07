package com.github.leapbound.yc.camunda;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.context.annotation.ComponentScan;


@EnableProcessApplication
@ComponentScan(basePackages = {"com.github.leapbound.yc.camunda"})
public class YcCamundaAutoConfiguration {
}
