package yzggy.yucong.service.impl;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.dialog.Conversation;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.FuncService;
import yzggy.yucong.service.GptService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final OpenAiClient openAiClient;
    private final ConversationService conversationService;
    private final FuncService funcService;

    @Override
    public void chat(SingleChatModel singleChatModel) {
        String botId = singleChatModel.getBotId();
        String userId = singleChatModel.getUserId();

        Conversation conversation = this.conversationService.getByUserId(userId);
        if (conversation == null) {
            this.conversationService.start(botId, userId);
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
    }

    private ChatChoice sendToChatServer(String botId, String userId) {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(this.conversationService.getByUserId(userId).getMessageList())
                .functions(this.funcService.getListByUserIdAndBotId(userId, botId))
                .functionCall("auto")
                .model(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName())
                .build();
        ChatCompletionResponse chatCompletionResponse = this.openAiClient.chatCompletion(chatCompletion);
        ChatChoice chatChoice = chatCompletionResponse.getChoices().get(0);
        log.info("sendToChatServer: {}", chatChoice);
        return chatChoice;
    }

}
