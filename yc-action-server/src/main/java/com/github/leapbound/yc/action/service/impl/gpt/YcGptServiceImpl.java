package com.github.leapbound.yc.action.service.impl.gpt;

import com.github.leapbound.yc.action.service.YcGptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YcGptServiceImpl implements YcGptService {

    private final ChatModel chatModel;

    @Override
    public List<Message> completions(List<Message> messageList, Boolean isTest) {
        return null;
    }

    @Override
    public String summary(String content) {
        return null;
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return null;
    }

}
