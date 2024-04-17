package com.github.leapbound.yc.camunda.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
                try {
                    JSONObject result = subService.execute(function, JSON.toJSONString(arguments));
                    logger.info("subService function [{}]  execute completed ", function);
                    if (result != null && !result.isEmpty()) {
                        //
                        Map<String, Object> afterArgs = new HashMap<>();
                        // reset variables
                        for (Map.Entry<String, Object> entry : result.entrySet()) {
                            //  execute functions after that service task completed
                            if (entry.getKey().equals("afterFunction")) {
                                if (entry.getValue() instanceof Map<?, ?>) {
                                    afterArgs = (Map<String, Object>) entry.getValue();
                                }
                                continue;
                            }
                            // set variables
                            execution.setVariable(entry.getKey(), entry.getValue());
                        }
                        // execute after functions
                        if (!afterArgs.isEmpty()) {
                            for (Map.Entry<String, Object> entry : afterArgs.entrySet()) {
                                subService.execute(entry.getKey(), JSON.toJSONString(entry.getValue()));
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.error("subService function [{}]  execute failed ", function, ex);
//                    throw new BpmnError("subService function [" + function + "] execute failed", ex);
                }
            } else {
                logger.warn("no [subService] found in the task");
            }
        } else {
            logger.warn("no [function] found in the task");
        }

    }
}
