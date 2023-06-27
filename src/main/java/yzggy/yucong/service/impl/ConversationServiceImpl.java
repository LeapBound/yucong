package yzggy.yucong.service.impl;

import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.dialog.Conversation;
import yzggy.yucong.service.ConversationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final Map<String, Conversation> conversationMap = new HashMap<>();

    @Override
    public Conversation getByUserId(String userId) {
        return this.conversationMap.get(userId);
    }

    @Override
    public Conversation start(String userId) {
        Conversation conversation = new Conversation();
        this.conversationMap.put(userId, conversation);

        // 系统消息，指定助理角色
        Message systemMsg = Message.builder()
                .role(Message.Role.SYSTEM)
                .content("你现在是一个客服人员")
                .build();
        addMessage(userId, systemMsg);

        return conversation;
    }

    @Override
    public void addMessage(String userId, Message... messages) {
        Conversation conversation = this.conversationMap.get(userId);
        if (conversation != null) {
            for (Message message : messages) {
                conversation.addMessage(message);
            }
        }
    }

    @Override
    public void addMessages(String userId, List<Message> messageList) {
        messageList.forEach(message -> addMessage(userId, message));
    }
}
