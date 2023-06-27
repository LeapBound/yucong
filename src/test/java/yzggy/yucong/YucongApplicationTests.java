package yzggy.yucong;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.GptService;

@Slf4j
@SpringBootTest
class YucongApplicationTests {

    @Autowired
    private GptService gptService;
    @Autowired
    private ConversationService conversationService;

    @Test
    void boost() {
        // 聊天模型：gpt-3.5
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setUserId("test001");
        singleChatModel.setContent("可以退货么");
        log.info("singleChatModel: {}", singleChatModel);
        this.gptService.chat(singleChatModel);
    }

    @Test
    void newArrival() {
        // 用户询问上新时间
        String userId = "test001";
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setUserId(userId);
        singleChatModel.setContent("请问什么时候上新");
        this.gptService.chat(singleChatModel);

        this.conversationService.getByUserId(userId).getMessageList().forEach(message ->
                log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
        );
    }
}
