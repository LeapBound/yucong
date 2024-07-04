package com.github.leapbound.yc.hub.service.gpt;

import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface GptService {

    List<MyMessage> completions(String botId, String accountId, Map<String, Object> params, List<MyMessage> messageList, Boolean isTest);

    String summary(String content);

    List<BigDecimal> embedding(String content);
}
