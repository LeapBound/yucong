package com.github.leapbound.yc.camunda.service;

import com.github.leapbound.yc.camunda.model.bo.ProcessStartRequest;
import com.github.leapbound.yc.camunda.model.bo.ProcessVariablesRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskCompleteRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskFindRequest;
import com.github.leapbound.yc.camunda.model.vo.ProcessStep;
import com.github.leapbound.yc.camunda.model.vo.TaskReturn;

import java.util.Map;

/**
 * @author yamath
 * @date 2024/3/28 14:36
 */
public interface CamundaService {

    /**
     * 发起一个流程
     *
     * @param processStartRequest 启动流程请求参数
     * @return 流程 id
     */
    String startProcess(ProcessStartRequest processStartRequest);

    /**
     * 发起一个流程并返回当前 step
     *
     * @param processStartRequest 启动流程请求参数
     * @return 流程
     */
    ProcessStep startProcessWithReturnTask(ProcessStartRequest processStartRequest);

    /**
     * 查找当前 task
     *
     * @param taskFindRequest task 查询请求
     * @return task 信息
     */
    TaskReturn findCurrentTask(TaskFindRequest taskFindRequest);

    /**
     * 完成当前 task
     *
     * @param taskCompleteRequest 完成 task 请求参数
     */
    void completeTask(TaskCompleteRequest taskCompleteRequest);

    /**
     * 完成当前 task 并进入下一步
     *
     * @param taskCompleteRequest 完成 task 请求参数
     * @return 流程
     */
    ProcessStep completeTaskWithReturn(TaskCompleteRequest taskCompleteRequest);

    TaskReturn cancelActivityToSpecify(TaskCompleteRequest taskCompleteRequest);

    Map<String, Object> inputProcessVariables(ProcessVariablesRequest processVariablesRequest);

    Map<String, Object> getProcessVariables(ProcessVariablesRequest processVariablesRequest);
}
