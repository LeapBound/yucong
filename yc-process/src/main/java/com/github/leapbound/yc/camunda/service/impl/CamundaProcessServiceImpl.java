package com.github.leapbound.yc.camunda.service.impl;

import com.github.leapbound.yc.camunda.service.CamundaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yamath
 * @date 2023/11/16 11:11
 */
@Slf4j
@Service
public class CamundaProcessServiceImpl implements CamundaProcessService {

    private final RuntimeService runtimeService;

    private final RepositoryService repositoryService;

    public CamundaProcessServiceImpl(RuntimeService runtimeService, RepositoryService repositoryService) {
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
    }

    @Override
    public void getProcessDefinitionList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        log.info("Process definition list: {}", list);
    }

    @Override
    public void getProcessDefinitionByKey(String key) {
        if (key == null || key.isEmpty()) {
            log.warn("process definition key cannot be null or empty");
            return;
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).singleResult();
        log.info("Process definition: {}", processDefinition);
    }

    @Override
    public String startProcessInstanceByKey(String key, Map<String, Object> variables) {
        if (key == null || key.isEmpty()) {
            log.warn("process definition key cannot be null or empty");
            return null;
        }
        ProcessInstance processInstance = null;
        if (variables == null || variables.isEmpty()) {
            processInstance = runtimeService.startProcessInstanceByKey(key);
        } else {
            processInstance = runtimeService.startProcessInstanceByKey(key, variables);
        }
        log.info("Process instance id: {}", processInstance.getProcessInstanceId());
        return processInstance.getProcessInstanceId();
    }
}
