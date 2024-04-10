package com.github.leapbound.yc.camunda.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yamath
 * @since 2024/4/7 10:08
 */
@Component
public class ServiceTaskDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTaskDelegate.class);

    private final ServiceTaskSubService subService;

    @Autowired(required = false)
    public ServiceTaskDelegate(ServiceTaskSubService subService) {
        this.subService = subService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String taskName = execution.getCurrentActivityName();
        logger.info("current task name: {} in process {}", taskName, processInstanceId);
        if (execution.hasVariableLocal("function")) {
            // get function
            String function = (String) execution.getVariableLocal("function");
            Map<String, Object> arguments = execution.getVariables();
            // has subService
            if (subService != null) {
                JSONObject result = subService.execute(function, JSON.toJSONString(arguments));
                logger.info("subService function [{}]  execute completed ", function);
                if (result != null && !result.isEmpty()) {
                    // reset variables
                    result.forEach(execution::setVariable);
                }
            } else {
                logger.warn("no [subService] found in the task");
            }
        } else {
            logger.warn("no [function] found in the task");
        }

    }
}
