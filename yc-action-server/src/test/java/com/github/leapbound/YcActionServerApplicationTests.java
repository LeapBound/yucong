package com.github.leapbound;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class YcActionServerApplicationTests {

    @Autowired
    ChatModel chatModel;

    @Test
    void contextLoads() {
    }

    @Test
    void springAiMiniMaxTest() {
        ChatResponse response = this.chatModel.call(
                new Prompt(
                        "你知道特朗普么",
                        MiniMaxChatOptions.builder()
                                .withModel(MiniMaxApi.ChatModel.ABAB_6_5_S_Chat.getValue())
                                .withTemperature(0.5)
                                .build()
                ));
        log.info("{}", response);
    }

}
