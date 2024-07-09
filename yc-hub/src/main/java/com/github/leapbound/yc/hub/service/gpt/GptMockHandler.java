package com.github.leapbound.yc.hub.service.gpt;


import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;

/**
 * @author Fred
 * @date 2024/5/22 23:00
 */
public interface GptMockHandler extends GptHandler {

    void setFunctionCall(MyFunctionCall myFunctionCall);
}
