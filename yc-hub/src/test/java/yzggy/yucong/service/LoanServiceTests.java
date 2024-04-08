package yzggy.yucong.service;

import com.github.leapbound.yc.hub.service.ConversationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.model.SingleChatDto;

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
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("我要申请一笔贷款");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("高志1 15781670616 130725199610121529");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("是的");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void applyStatus() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);

        singleChatModel.setContent("请问我的贷款什么进度了");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("预审订单");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void bindCard() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);

        singleChatModel.setContent("卡号是6227003815977406710，预留手机号15781670614");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("验证码是123456");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void withdraw() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);

        singleChatModel.setContent("我要提取3333.3元，分3期");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("对的");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void currentRepay() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("看一下这个订单要还多少钱");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("FCS01-170217-248418");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void makeLoanStatus() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("看下这个订单什么时候放款");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("MLB01-230809-471196");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void loanStatus() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("看下这个订单现在什么状态");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("GEX01-230414-859298");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void tryRepay() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("提前还款费用");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("GEX01-230414-859298");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void tryRefund() {
        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("麻烦看下这个订单退贷费用");
        this.conversationService.chat(singleChatModel);
        singleChatModel.setContent("GEX01-230414-859298");
        this.conversationService.chat(singleChatModel);
    }

}
