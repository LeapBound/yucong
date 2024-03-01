package com.github.leapbound.yc.camunda.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserTaskJobHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        log.info("UserTaskJobHandler execute start");
        log.info("variable self: {}", delegateExecution.getVariable("self"));
        delegateExecution.setVariable("self", true);
    }
}
