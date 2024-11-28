package com.github.leapbound.yc.action.service;


import org.springframework.ai.chat.messages.Message;

import java.math.BigDecimal;
import java.util.List;

public interface YcGptService {

    List<Message> completions(List<Message> messageList, Boolean isTest);

    String summary(String content);

    List<BigDecimal> embedding(String content);
}
