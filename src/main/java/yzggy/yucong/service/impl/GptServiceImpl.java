package yzggy.yucong.service.impl;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.dialog.Conversation;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.FuncService;
import yzggy.yucong.service.GptService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final OpenAiClient openAiClient;
    private final ConversationService conversationService;
    private final FuncService funcService;

    @Override
    public String chat(SingleChatModel singleChatModel) {
        String botId = singleChatModel.getBotId();
        String userId = singleChatModel.getAccountId();

        Conversation conversation = this.conversationService.getByAccountId(userId);
        if (conversation == null) {
            conversation = this.conversationService.start(botId, userId);
            if (conversation == null) {
                return "该bot没有调用权限";
            }
        }

        // 客户消息
        Message userMsg = Message.builder()
                .role(Message.Role.USER)
                .content(singleChatModel.getContent())
                .build();
        this.conversationService.addMessage(userId, userMsg);

        ChatChoice chatChoice = sendToChatServer(botId, userId);

        // 处理方法调用
        if (chatChoice.getMessage().getFunctionCall() != null) {
            this.funcService.invokeFunc(userId, chatChoice.getMessage().getFunctionCall());
            chatChoice = sendToChatServer(botId, userId);
        }

        // 助理消息
        Message assistantMsg = Message.builder()
                .role(chatChoice.getMessage().getRole())
                .content(chatChoice.getMessage().getContent())
                .build();
        this.conversationService.addMessage(userId, assistantMsg);

        return chatChoice.getMessage().getContent();
    }

    private ChatChoice sendToChatServer(String botId, String accountId) {
        ChatCompletion.ChatCompletionBuilder chatCompletionBuilder = ChatCompletion.builder()
                .messages(this.conversationService.getByAccountId(accountId).getMessageList())
                .model(ChatCompletion.Model.GPT_3_5_TURBO_0613.getName());
        List<Functions> functionsList = this.funcService.getListByAccountIdAndBotId(accountId, botId);
        if (functionsList != null && functionsList.size() > 0) {
            chatCompletionBuilder
                    .functionCall("auto")
                    .functions(functionsList);
        }

        ChatCompletionResponse chatCompletionResponse = this.openAiClient.chatCompletion(chatCompletionBuilder.build());
        ChatChoice chatChoice = chatCompletionResponse.getChoices().get(0);
        log.info("sendToChatServer 返回结果: {}", chatChoice);
        return chatChoice;
    }
}
