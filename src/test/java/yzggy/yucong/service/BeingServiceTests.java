package yzggy.yucong.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.gpt.GptService;
import yzggy.yucong.service.gpt.MilvusService;

import java.util.List;

@Slf4j
@SpringBootTest
public class BeingServiceTests {

    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MilvusService milvusService;

    private final String botId = "bot003";
    private final String accountId = "account003";

    @BeforeEach
    void clearHistory() {
        this.conversationService.clearMessageHistory(this.botId, this.accountId);
    }

    @AfterEach
    void printLog() {
        List<MyMessage> messageList = this.conversationService.getByBotIdAndAccountId(this.botId, this.accountId);
        if (messageList != null) {
            messageList.forEach(message ->
                    log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
            );
        }
    }

    @Test
    void createMilvusCollection() {
        this.milvusService.dropCollection();
        this.milvusService.createCollection();
    }

    @Test
    void doSummary() {
        this.conversationService.summaryDialog("dbc6eea177934be8b7eb0523a19489d5");
    }

    @Test
    void chatWithHistory() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);

        singleChatModel.setContent("超级篮球场多少钱一小时");
        this.conversationService.chat(singleChatModel);
    }

    @Test
    void startChat() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);

        singleChatModel.setContent("你好");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("今天天气怎么样");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("那我去打个篮球吧，有推荐的运动场所吗");
        this.conversationService.chat(singleChatModel);

        singleChatModel.setContent("好的，帮我预定一下下午两点的场地吧");
        this.conversationService.chat(singleChatModel);
    }

}
