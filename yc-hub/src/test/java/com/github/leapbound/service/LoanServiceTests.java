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

    private final String botId = "B6aeff8084b134aaeba2d919270f8322a";
    private final String accountId = "A95b28a1a41024b5ca1b8053996d24cb5";

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

    @Test
    void checkProcessAvailable() throws InterruptedException {
        ProcessTaskDto processTaskDto = null;
        while (processTaskDto == null) {
            log.warn("checkProcessAvailable");
            Thread.sleep(3000);
            processTaskDto = this.actionServerService.queryNextTask(this.accountId);
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
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("今天天气怎么样");
        log.info("gossip {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void applyLoan() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("我要申请一笔贷款");
        log.info("applyLoan {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void bindMobile() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("13801234567");
        log.info("bindMobile {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void verifyCode() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("123456");
        log.info("verifyCode {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void bdMobile() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("13818634281");
        log.info("bdMobile {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void productInfo() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("水光针 1万块");
        log.info("productInfo {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void chooseProduct() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("第一个");
        log.info("chooseProduct {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void idPhotoFront() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("image");
        singleChatModel.setPicUrl("https://beta.geexfinance.com/group2/M00/AA/AA/wKhvEmYLaduAPH4aAAYwDFtNjj8213.jpg");
        log.info("idPhotoFront {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void idPhotoBack() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("image");
        singleChatModel.setPicUrl("https://beta.geexfinance.com/group2/M00/AA/AB/wKhvEmYLgcOAUhZFAAJmq0Eow-0052.jpg");
        log.info("idPhotoBack {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void bankCard() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("8703003892904753 15913175493 浦发银行");
        log.info("bankCard {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void protocolVerifyCode() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("123456");
        log.info("protocolVerifyCode {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void contractPreview() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("看过了");
        log.info("contractPreview {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void maritalStatus() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("已婚");
        log.info("maritalStatus {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void relationInfo() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("张三 13666666666");
        log.info("relationInfo {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void relation() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("第二个");
        log.info("relation {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void forthStep() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("沃尔玛公司 上海市静安区110号3楼");
        log.info("forthStep {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void finishFace() {
        log.info("*".repeat(100));
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);
        singleChatModel.setType("text");
        singleChatModel.setContent("做完了");
        log.info("finishFace {}", this.conversationService.chat(singleChatModel));
    }

    @Test
    void testLoan() throws InterruptedException {
        deleteProcess();

        applyLoan();
        bindMobile();
        checkProcessAvailable();

        verifyCode();
        bdMobile();
        productInfo();
        checkProcessAvailable();

        chooseProduct();
        checkProcessAvailable();

        idPhotoFront();
        idPhotoBack();
        checkProcessAvailable();

        bankCard();
        checkProcessAvailable();

        protocolVerifyCode();
        checkProcessAvailable();

        contractPreview();
        checkProcessAvailable();

        maritalStatus();
        relationInfo();
        relation();
        forthStep();
        checkProcessAvailable();

        finishFace();
    }
}