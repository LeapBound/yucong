package yzggy.yucong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yzggy.yucong.chat.dialog.Conversation;
import yzggy.yucong.consts.MqConsts;
import yzggy.yucong.entities.BotEntity;
import yzggy.yucong.entities.MessageEntity;
import yzggy.yucong.mapper.BotMapper;
import yzggy.yucong.mapper.MessageMapper;
import yzggy.yucong.service.ConversationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final BotMapper botMapper;
    private final MessageMapper messageMapper;
    private final RedisTemplate<Object, Object> redisTemplate;
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

        // 初始化角色定义
        if (StringUtils.hasText(botEntity.getInitRoleContent())) {
            Message systemMsg = Message.builder()
                    .role(Message.Role.SYSTEM)
                    .content(botEntity.getInitRoleContent())
                    .build();
            addMessage(userId, systemMsg);
        }

        return conversation;
    }

    @Override
    public void addMessage(String accountId, Message... messages) {
        addMessages(accountId, List.of(messages));
    }

    @Override
    public void addMessages(String accountId, List<Message> messageList) {
        Conversation conversation = this.conversationMap.get(accountId);
        if (conversation != null) {
            messageList.forEach(conversation::addMessage);
            sendPersistMessageMq(messageList);
        }
    }

    @Override
    public void persistMessage(List<Message> messageList) {
//        List<MessageEntity> entityList = new ArrayList<>(messageList.size());
        messageList.forEach(message -> {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setRole(message.getRole());
            messageEntity.setContent(message.getContent());
            this.messageMapper.insert(messageEntity);
        });
    }

    @Override
    public void persistMessageMap(List<Map<String, Object>> messageList) {
//        List<MessageEntity> entityList = new ArrayList<>(messageList.size());
        messageList.forEach(message -> {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setRole(message.get("role").toString());
            messageEntity.setContent(message.get("content").toString());
            this.messageMapper.insert(messageEntity);
        });
    }

    private void sendPersistMessageMq(List<Message> messageList) {
        this.redisTemplate.convertAndSend(MqConsts.MQ_CHAT_MESSAGE, messageList);
    }
}
