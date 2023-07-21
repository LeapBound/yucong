package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.Functions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yzggy.yucong.model.SingleChatModel;

import java.util.List;

@Slf4j
@SpringBootTest
public class ServiceTests {

    @Autowired
    private FuncService funcService;
    @Autowired
    private GptService gptService;
    @Autowired
    private ConversationService conversationService;

    private final String botId = "bot001";
    private final String accountId = "account001";

    @Test
    public void getFuncList() {
        List<Functions> functions = this.funcService.getListByAccountIdAndBotId(accountId, botId);
        log.info("{}", functions);
    }

    @Test
    void setNewArrival() {
        // 设置上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("设置一下上新时间");
        this.gptService.chat(singleChatModel);
    }

    @Test
    void newArrival() {
        // 用户询问上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("请问什么时候上新");
        this.gptService.chat(singleChatModel);
    }

    @Test
    void closeAccount() {
        // 用户询问上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("关闭离职人员账号");
        this.gptService.chat(singleChatModel);
        singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("账号是geex001");
        this.gptService.chat(singleChatModel);
    }

    @Test
    void applyLoan() {
        // 用户询问上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent("办理个客人的分期");
        this.gptService.chat(singleChatModel);
        singleChatModel.setContent("张雨绮 18012209999 310110200011218888");
        this.gptService.chat(singleChatModel);
        singleChatModel.setContent("24000");
        this.gptService.chat(singleChatModel);
    }

    @AfterEach
    void printLog() {
        if (this.conversationService.getByAccountId(accountId) != null) {
            this.conversationService.getByAccountId(accountId).getMessageList().forEach(message ->
                    log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
            );
        }
    }

}
