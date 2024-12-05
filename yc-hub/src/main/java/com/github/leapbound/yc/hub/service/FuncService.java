package com.github.leapbound.yc.hub.service;

import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.sdk.llm.chat.func.MyFunctions;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;

import java.util.List;

/**
 * @author Fred Gu
 * @date 2024-12-04 11:26
 */
public interface FuncService {

    List<MyFunctions> getListByAccountIdAndBotId(String accountName, String botId, ProcessTaskDto currentTask);

    FunctionExecResultDto invokeFunc(String botId, String accountId, MyFunctionCall functionCall);

}
