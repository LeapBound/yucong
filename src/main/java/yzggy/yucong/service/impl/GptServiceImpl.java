package yzggy.yucong.service.impl;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.FuncService;
import yzggy.yucong.service.GptHandler;
import yzggy.yucong.service.GptService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final ConversationService conversationService;
    private final FuncService funcService;
    private final GptHandler openAiHandler;
    private final GptHandler qianfanHandler;

    @Override
    public String chat(SingleChatModel singleChatModel) {
        String botId = singleChatModel.getBotId();
        String accountId = singleChatModel.getAccountId();

        List<MyMessage> messageList = this.conversationService.getByBotIdAndAccountId(botId, accountId);
        if (messageList == null) {
            if (!this.conversationService.start(botId, accountId)) {
                return "该bot没有调用权限";
            }
        }

        // 客户消息
        MyMessage userMsg = new MyMessage();
        userMsg.setRole(Message.Role.USER.getName());
        userMsg.setContent(singleChatModel.getContent());
        this.conversationService.addMessage(botId, accountId, userMsg);

        MyChatCompletionResponse response = sendToChatServer(botId, accountId);

        // 处理方法调用
        if (response.getFunctionCall() != null) {
            this.funcService.invokeFunc(botId, accountId, response.getFunctionCall());
            response = sendToChatServer(botId, accountId);
        }

        // 助理消息
        MyMessage assistantMsg = new MyMessage();
        assistantMsg.setRole(response.getMessage().getRole());
        assistantMsg.setContent(response.getMessage().getContent());
        this.conversationService.addMessage(botId, accountId, assistantMsg);

        return response.getMessage().getContent();
    }

    @Override
    public String summary(String content) {
        return this.openAiHandler.summary(content).getMessage().getContent();
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return this.openAiHandler.embedding(content);
    }

    private MyChatCompletionResponse sendToChatServer(String botId, String accountId) {
        List<MyMessage> messageList = this.conversationService.getByBotIdAndAccountId(botId, accountId);
        List<MyFunctions> functionsList = this.funcService.getListByAccountIdAndBotId(accountId, botId);
        return this.openAiHandler.chatCompletion(messageList, functionsList);
//        return this.qianfanHandler.chatCompletion(messageList, functionsList);
    }
}
