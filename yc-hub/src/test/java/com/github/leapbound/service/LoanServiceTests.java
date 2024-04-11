package com.github.leapbound.service;

import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.ActionServerService;
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
public class LoanServiceTests {

    @Autowired
    ConversationService conversationService;
    @Autowired
    ActionServerService actionServerService;

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
            messageList.forEach(message ->
                    log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
            );
        }
    }

    @Test
    void deleteProcess() {
        ProcessTaskDto processTaskDto = this.actionServerService.queryNextTask(this.accountId);
        if (processTaskDto != null && processTaskDto.getProcessInstanceId() != null) {
            this.actionServerService.deleteProcess(processTaskDto.getProcessInstanceId());
        }
    }

    @Test
    void gossip() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("今天天气怎么样");
        log.info("*** gossip {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void applyLoan() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("我要申请一笔贷款");
        log.info("*** applyLoan {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void bindMobile() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("13666666666");
        log.info("*** bindMobile {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void verifyCode() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("123456");
        log.info("*** verifyCode {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void bdMobile() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("13818634281");
        log.info("*** bdMobile {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void productInfo() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("水光针 1万块");
        log.info("*** productInfo {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void chooseProduct() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("第一个");
        log.info("*** chooseProduct {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void testLoan() {
        deleteProcess();

        applyLoan();
        bindMobile();
        verifyCode();
        bdMobile();
        productInfo();
        chooseProduct();
    }

}
