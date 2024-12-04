package com.github.leapbound.yc.hub.service;

import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;

/**
 * @author Fred
 * @date 2024/4/8 11:26
 */
public interface YcActionServerService {

    FunctionExecResultDto invokeFunc(String botId, String accountId, MyFunctionCall functionCall);

}
