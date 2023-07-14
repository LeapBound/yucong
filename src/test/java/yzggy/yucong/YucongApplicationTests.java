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
    void setNewArrival() {
        String userId = "admin001";
        String botId = "bot001";

        // 设置上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setUserId(userId);
        singleChatModel.setContent("设置一下上新时间");
        this.gptService.chat(singleChatModel);

        this.conversationService.getByUserId(userId).getMessageList().forEach(message ->
                log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
        );
    }

    @Test
    void newArrival() {
        String userId = "test001";
        String botId = "bot001";

        // 用户询问上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setUserId(userId);
        singleChatModel.setContent("请问什么时候上新");
        this.gptService.chat(singleChatModel);

        this.conversationService.getByUserId(userId).getMessageList().forEach(message ->
                log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
        );
    }

    @Test
    void closeAccount() {
        String userId = "test001";
        String botId = "bot001";

        // 用户询问上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setUserId(userId);
        singleChatModel.setContent("关闭离职人员账号");
        this.gptService.chat(singleChatModel);
        singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setUserId(userId);
        singleChatModel.setContent("账号是geex001");
        this.gptService.chat(singleChatModel);

        this.conversationService.getByUserId(userId).getMessageList().forEach(message ->
                log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
        );
    }

    @Test
    void applyLoan() {
        String userId = "test001";
        String botId = "bot002";

        // 用户询问上新时间
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setUserId(userId);
        singleChatModel.setContent("办理个客人的分期");
        this.gptService.chat(singleChatModel);
        singleChatModel.setContent("张雨绮 18012209999 310110200011218888");
        this.gptService.chat(singleChatModel);
        singleChatModel.setContent("24000");
        this.gptService.chat(singleChatModel);

        this.conversationService.getByUserId(userId).getMessageList().forEach(message ->
                log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
        );
    }
}
