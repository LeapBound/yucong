package com.github.leapbound.yc.hub.service.impl.gpt;

import com.github.leapbound.yc.hub.chat.dialog.MyChatCompletionResponse;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.chat.func.MyFunctions;
import com.github.leapbound.yc.hub.service.gpt.GptMockHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Fred
 * @date 2024/5/22 22:28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MockHandler implements GptMockHandler {

    private static final ThreadLocal<MyFunctionCall> FUNCTION_CALL = new ThreadLocal<>();

    @Override
    public MyChatCompletionResponse chatCompletion(List<MyMessage> messageList, List<MyFunctions> functionsList) {
        log.info("message {}, function {}", messageList.get(messageList.size() - 1), functionsList);

        MyFunctionCall functionCall = getMyFunctionCall();

        MyMessage message = new MyMessage();
        message.setName(functionCall.getName());
        message.setFunctionCall(functionCall);
        message.setRole("assistant");

        MyChatCompletionResponse response = new MyChatCompletionResponse();
        response.setMessage(message);

        return response;
    }

    private MyFunctionCall getMyFunctionCall() {
        MyFunctionCall functionCall = new MyFunctionCall();
        functionCall.setName(FUNCTION_CALL.get().getName());
        functionCall.setArguments(FUNCTION_CALL.get().getArguments());
        setFunctionCall(null);
        return functionCall;
    }

    @Override
    public MyChatCompletionResponse summary(String content) {
        return null;
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return List.of();
    }

    @Override
    public void setFunctionCall(MyFunctionCall myFunctionCall) {
        FUNCTION_CALL.set(myFunctionCall);
    }
}
