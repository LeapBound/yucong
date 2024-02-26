package com.github.leapbound.yc.hub.service.gpt;

import com.github.leapbound.yc.hub.chat.dialog.MyMessage;

import java.math.BigDecimal;
import java.util.List;

public interface GptService {

    List<MyMessage> completions(String botId, String accountId, List<MyMessage> messageList);

    String summary(String content);

    List<BigDecimal> embedding(String content);
}
