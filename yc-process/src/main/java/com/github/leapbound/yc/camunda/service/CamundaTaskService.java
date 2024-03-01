package com.github.leapbound.yc.camunda.service;

import org.camunda.bpm.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * @author yamath
 * @since 2023/11/16 10:56
 */
public interface CamundaTaskService {

    List<Task> getTaskList(String processInstanceId);

    Task getTaskById(String id);

    Map<String, Object> getTaskFormVariables(String id);

    void completeTask(String id, Map<String, Object> params, boolean withReturn);
}
