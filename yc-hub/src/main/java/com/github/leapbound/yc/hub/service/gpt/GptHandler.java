package com.github.leapbound.yc.hub.service.gpt;

import com.github.leapbound.yc.hub.chat.dialog.MyChatCompletionResponse;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.func.MyFunctions;

import java.math.BigDecimal;
import java.util.List;

public interface GptHandler {

    MyChatCompletionResponse chatCompletion(List<MyMessage> messageList, List<MyFunctions> functionsList);

    MyChatCompletionResponse summary(String content);

    List<BigDecimal> embedding(String content);
}
