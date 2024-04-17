package com.github.leapbound.yc.hub.service;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;

import java.util.Set;

/**
 * @author Fred
 * @date 2024/4/8 11:26
 */
public interface ActionServerService {

    ProcessTaskDto queryNextTask(String accountId);

    String getProcessTaskRemind(String accountId, ProcessTaskDto currentTask, Boolean functionExecuteResult);

    JSONObject loadProcessVariables(String processInstanceId);

    Set<String> loadTaskFunctionOptions(ProcessTaskDto task);

    void deleteProcess(String processInstanceId);

    Boolean invokeFunc(String botId, String accountId, MyFunctionCall functionCall);

}
