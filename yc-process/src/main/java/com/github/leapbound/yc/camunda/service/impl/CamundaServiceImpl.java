package com.github.leapbound.yc.camunda.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.leapbound.yc.camunda.model.bo.ProcessStartRequest;
import com.github.leapbound.yc.camunda.model.bo.ProcessVariablesRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskCompleteRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskFindRequest;
import com.github.leapbound.yc.camunda.model.vo.ProcessStep;
import com.github.leapbound.yc.camunda.model.vo.R;
import com.github.leapbound.yc.camunda.model.vo.TaskReturn;
import com.github.leapbound.yc.camunda.service.CamundaService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author yamath
 * @date 2024/3/28 14:39
 */
@Service
public class CamundaServiceImpl implements CamundaService {

    private static final Logger logger = LoggerFactory.getLogger(CamundaServiceImpl.class);
    private final RepositoryService repositoryService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final FormService formService;

    public CamundaServiceImpl(RepositoryService repositoryService,
                              RuntimeService runtimeService,
                              TaskService taskService,
                              FormService formService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.formService = formService;
    }

    @Override
    public String startProcess(ProcessStartRequest processStartRequest) {
        return null;
    }

    @Override
    public ProcessStep startProcessWithReturnTask(ProcessStartRequest processStartRequest) {
        return null;
    }

    @Override
    public TaskReturn findCurrentTask(TaskFindRequest taskFindRequest) {
        return null;
    }

    @Override
    public void completeTask(TaskCompleteRequest taskCompleteRequest) {

    }

    @Override
    public ProcessStep completeTaskWithReturn(TaskCompleteRequest taskCompleteRequest) {
        return null;
    }

    @Override
    public TaskReturn cancelActivityToSpecify(TaskCompleteRequest taskCompleteRequest) {
        return null;
    }

    @Override
    public Map<String, Object> inputProcessVariables(ProcessVariablesRequest processVariablesRequest) {
        return null;
    }

    @Override
    public Map<String, Object> getProcessVariables(ProcessVariablesRequest processVariablesRequest) {
        return null;
    }
}
