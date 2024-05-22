package com.github.leapbound.service;

import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.ConversationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class TicketServiceTests {

    @Autowired
    ConversationService conversationService;

    private final String botId = "bot001";
    private final String accountId = "account001";

    @BeforeEach
    void clearHistory() {
        this.conversationService.clearMessageHistory(this.botId, this.accountId);
    }

    @AfterEach
    void printLog() {
        List<MyMessage> messageList = this.conversationService.getByBotIdAndAccountId(botId, accountId);
        if (messageList != null) {
            log.info("#".repeat(100));
            messageList.forEach(message ->
                    log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
            );
        }
    }

    private void chooseService() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("你好，麻烦看看这个是什么情况");
        log.info("chooseService {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void testTicket() throws InterruptedException {
        chooseService();
    }
}
