package com.github.leapbound.yc.hub.service;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;

/**
 * @author Fred
 * @date 2024/4/8 11:26
 */
public interface ActionServerService {

    ProcessTaskDto queryNextTask(String accountId);

    JSONObject loadProcessConfig(String processInstanceId);

    MyMessage invokeFunc(String botId, String accountId, MyFunctionCall functionCall);
}
