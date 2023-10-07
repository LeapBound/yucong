package yzggy.yucong.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.gpt.FuncService;
import yzggy.yucong.service.gpt.GptService;

import java.util.List;

@Slf4j
@SpringBootTest
public class LoanServiceTests {

    @Autowired
    protected ConversationService conversationService;

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
    void applyLoan() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("办理个客人的分期");
        this.conversationService.chat(singleChatModel);

//        singleChatModel.setContent("张雨绮 18012209999 310110200011218888");
//        this.gptService.chat(singleChatModel);
//        singleChatModel.setContent("24000");
//        this.gptService.chat(singleChatModel);
    }

    @Test
    void currentRepay() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("看一下这个订单要还多少钱");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("FCS01-170217-248418");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void makeLoanStatus() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("看下这个订单什么时候放款");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("MLB01-230809-471196");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void loanStatus() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("看下这个订单现在什么状态");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("GEX01-230414-859298");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void tryRepay() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("提前还款费用");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("GEX01-230414-859298");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void tryRefund() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("麻烦看下这个订单退贷费用");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("GEX01-230414-859298");
        this.conversationService.chat(singleChatModel);
    }

}
