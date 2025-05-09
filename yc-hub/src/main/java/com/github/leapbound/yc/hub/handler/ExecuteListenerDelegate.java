package com.github.leapbound.yc.hub.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yamath
 * @date 2024/4/12 11:25
 */
@Slf4j
@Component
public class ExecuteListenerDelegate implements ExecutionListener {

    private final ServiceTaskSubService subService;

    private Expression function;

    @Autowired(required = false)
    public ExecuteListenerDelegate(ServiceTaskSubService subService) {
        this.subService = subService;
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        if (function != null) {
            String method = (String) function.getValue(execution);
            try {
                Map<String, Object> arguments = execution.getVariables();
                JSONObject result = subService.execute(method, JSON.toJSONString(arguments));
                log.info("subService function [{}]  execute completed ", method);
                if (result != null && !result.isEmpty()) {
                    //
                    Map<String, Object> afterArgs = new HashMap<>();
                    Map<String, Object> variables = new HashMap<>();
                    // reset variables
                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        //  execute functions after that service task completed
                        if (entry.getKey().equals("afterFunction")) {
                            if (entry.getValue() instanceof Map<?, ?>) {
                                afterArgs = (Map<String, Object>) entry.getValue();
                            }
                        } else {
                            variables.put(entry.getKey(), entry.getValue());
                        }
                    }
                    if (!variables.isEmpty()) {
                        // set variables
                        execution.setVariables(variables);
                    }
                    // execute after functions
                    if (!afterArgs.isEmpty()) {
                        for (Map.Entry<String, Object> entry : afterArgs.entrySet()) {
                            subService.execute(entry.getKey(), JSON.toJSONString(entry.getValue()));
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("subService function [{}]  execute failed ", function, ex);
//                    throw new BpmnError("subService function [" + function + "] execute failed", ex);
            }
        }
    }
}
