package com.github.leapbound.yc.camunda;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;


@EnableProcessApplication
@ComponentScan(basePackages = {"com.github.leapbound.yc.camunda"})
@MapperScan(basePackages = {"com.github.leapbound.yc.camunda.mapper"})
public class YcCamundaAutoConfiguration {
}
