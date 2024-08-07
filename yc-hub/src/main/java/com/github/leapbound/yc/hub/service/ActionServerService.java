package com.github.leapbound.yc.hub.service;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;

import java.util.Map;
import java.util.Set;

/**
 * @author Fred
 * @date 2024/4/8 11:26
 */
public interface ActionServerService {

    ProcessTaskDto queryNextTask(String accountId);

    String getProcessTaskRemind(String accountId, ProcessTaskDto currentTask, FunctionExecResultDto functionExecuteResult);

    JSONObject loadProcessVariables(String processInstanceId);

    Set<String> loadTaskFunctionOptions(ProcessTaskDto task);

    String getTaskFunction(ProcessTaskDto task);

    void deleteProcess(String processInstanceId);

    void inputProcessVariable(String processInstanceId, String businessKey, Map<String, Object> params);

    FunctionExecResultDto invokeFunc(String botId, String accountId, MyFunctionCall functionCall);

}
