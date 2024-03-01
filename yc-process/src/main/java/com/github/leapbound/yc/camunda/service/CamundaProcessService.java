package com.github.leapbound.yc.camunda.service;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/11/16 10:55
 */
public interface CamundaProcessService {

    void getProcessDefinitionList();
    void getProcessDefinitionByKey(String key);
    String startProcessInstanceByKey(String key, Map<String, Object> variables);

}
