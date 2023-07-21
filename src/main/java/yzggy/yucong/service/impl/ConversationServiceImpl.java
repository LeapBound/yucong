package yzggy.yucong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.dialog.Conversation;
import yzggy.yucong.entities.BotEntity;
import yzggy.yucong.mapper.BotMapper;
import yzggy.yucong.service.ConversationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final BotMapper botMapper;
    private final Map<String, Conversation> conversationMap = new HashMap<>();

    @Override
    public Conversation getByAccountId(String accountId) {
        return this.conversationMap.get(accountId);
    }

    @Override
    public Conversation start(String botId, String userId) {
        // 系统消息，指定助理角色
        LambdaQueryWrapper<BotEntity> botLQW = new LambdaQueryWrapper<BotEntity>()
                .eq(BotEntity::getBotId, botId)
                .last("limit 1");
        BotEntity botEntity = this.botMapper.selectOne(botLQW);
        if (botEntity == null) {
            return null;
        }

        Conversation conversation = new Conversation();
        this.conversationMap.put(userId, conversation);
        Message systemMsg = Message.builder()
                .role(Message.Role.SYSTEM)
                .content(botEntity.getInitRoleContent())
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
