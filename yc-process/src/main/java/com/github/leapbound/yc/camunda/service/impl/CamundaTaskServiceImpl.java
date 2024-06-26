package com.github.leapbound.yc.camunda.service.impl;

import com.github.leapbound.yc.camunda.service.CamundaTaskService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.VariableMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yamath
 * @date 2023/11/16 11:11
 */
@Slf4j
@Service
public class CamundaTaskServiceImpl implements CamundaTaskService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;

    public CamundaTaskServiceImpl(TaskService taskService, RuntimeService runtimeService) {
        this.taskService = taskService;
        this.runtimeService = runtimeService;
    }

    @Override
    public List<Task> getTaskList(String processInstanceId) {
        List<Task> list =
                taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        log.info("Task list: {}", list);
        return list;
    }

    @Override
    public Task getTaskById(String id) {
        if (id == null || id.isEmpty()) {
            log.warn("Task id cannot be null or empty");
            return null;
        }
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        log.info("Task: {}", task);
        return task;
    }

    @Override
    public Map<String, Object> getTaskFormVariables(String id) {
        if (id == null || id.isEmpty()) {
            log.warn("Task id cannot be null or empty");
            return null;
        }
        Map<String, Object> map = taskService.getVariables(id);
        log.info("Task form variables: {}", map);
        return map;
    }

    @Override
    public void completeTask(String id, Map<String, Object> params, boolean withReturn) {
        if (id == null || id.isEmpty()) {
            log.warn("Task id cannot be null or empty");
            return;
        }
        if (withReturn) {
            VariableMap variableMap = taskService.completeWithVariablesInReturn(id, params, false);
            for (String key : variableMap.keySet()) {
                log.info("Complete task with return: {}", variableMap.get(key));
            }
        } else {
            taskService.complete(id, params);
            log.info("Complete task");
        }

    }
}
