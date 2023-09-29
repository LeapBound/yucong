package yzggy.yucong.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.model.SingleChatModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class BeingServiceTests {

    @Autowired
    private GptService gptService;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MilvusService milvusService;

    private final String botId = "bot003";
    private final String accountId = "account002";

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
        List<BigDecimal> embedding = this.gptService.embedding("assistant: 喵喵~你好呀！有什么我可以帮助你的吗？\n" +
                "user: 今天天气怎么样\n" +
                "assistant: 喵~天气晴朗，温度为28度，十分适宜\n" +
                "user: 那我去打个篮球吧，有推荐的运动场所吗\n" +
                "assistant: 喵~附近有一家叫超级篮球场的体育馆，费用为每小时20元，需要帮你预定吗\n" +
                "user: 好的，帮我预定一下下午两点的场地吧\n" +
                "assistant: 抱歉，作为一个虚拟助手，我无法为你进行实时的场地预定喵。记得提前预定场地，以确保你能在下午两点有一个篮球场地可用喵！祝你打球愉快喵~");
        List<Float> floatList = new ArrayList<>(embedding.size());
        embedding.forEach(item -> floatList.add(item.floatValue()));
        this.milvusService.insertData(1, floatList);
    }

    @Test
    void startChat() {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(this.botId);
        singleChatModel.setAccountId(this.accountId);

        singleChatModel.setContent("你好");
        this.gptService.chat(singleChatModel);

        singleChatModel.setContent("今天天气怎么样");
        this.gptService.chat(singleChatModel);

        singleChatModel.setContent("那我去打个篮球吧，有推荐的运动场所吗");
        this.gptService.chat(singleChatModel);

        singleChatModel.setContent("好的，帮我预定一下下午两点的场地吧");
        this.gptService.chat(singleChatModel);
    }

}
