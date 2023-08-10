package yzggy.yucong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yzggy.yucong.chat.dialog.MessageMqTrans;
import yzggy.yucong.consts.MqConsts;
import yzggy.yucong.entities.BotEntity;
import yzggy.yucong.entities.MessageEntity;
import yzggy.yucong.mapper.BotMapper;
import yzggy.yucong.mapper.MessageMapper;
import yzggy.yucong.service.ConversationService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final BotMapper botMapper;
    private final MessageMapper messageMapper;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper mapper;
    private final String ACCOUNT_MAP_KEY = "account.conversation.map";
    private final int EXPIRES = 600;

    @Override
    public List<Message> getByConversationId(String conversationId) {
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(conversationId))) {
            return null;
        }

        List<Object> objList = this.redisTemplate.opsForList().range(conversationId, 0, -1);
        List<Message> messageList = new ArrayList<>(objList.size());
        objList.forEach(o -> messageList.add(mapper.convertValue(o, new TypeReference<>() {
        })));
        return messageList;
    }

    @Override
    public List<Message> getByBotIdAndAccountId(String botId, String accountId) {
        String mapKey = ACCOUNT_MAP_KEY + botId + accountId;
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(mapKey))) {
            return null;
        }

        String conversationId = (String) this.redisTemplate.opsForHash().get(mapKey, "conversationId");
        if (!StringUtils.hasText(conversationId)) {
            return null;
        }

        return getByConversationId(conversationId);
    }

    @Override
    public Boolean start(String botId, String accountId) {
        // 系统消息，指定助理角色
        LambdaQueryWrapper<BotEntity> botLQW = new LambdaQueryWrapper<BotEntity>()
                .eq(BotEntity::getBotId, botId)
                .last("limit 1");
        BotEntity botEntity = this.botMapper.selectOne(botLQW);
        if (botEntity == null) {
            return false;
        }

        String conversationId = generateConversationId();
        String mapKey = ACCOUNT_MAP_KEY + botId + accountId;
        this.redisTemplate.opsForHash().put(mapKey, "conversationId", conversationId);
        this.redisTemplate.expire(mapKey, Duration.ofSeconds(EXPIRES));

        // 初始化角色定义
        if (StringUtils.hasText(botEntity.getInitRoleContent())) {
            Message systemMsg = Message.builder()
                    .role(Message.Role.SYSTEM)
                    .content(botEntity.getInitRoleContent())
                    .build();
            addMessage(conversationId, botId, accountId, systemMsg);
        }

        return true;
    }

    @Override
    public void addMessage(String botId, String accountId, Message message) {
        String mapKey = ACCOUNT_MAP_KEY + botId + accountId;
        this.redisTemplate.expire(mapKey, Duration.ofSeconds(EXPIRES));
        String conversationId = (String) this.redisTemplate.opsForHash().get(mapKey, "conversationId");
        addMessage(conversationId, botId, accountId, message);
    }

    @Override
    public void clearMessageHistory(String botId, String accountId) {
        String mapKey = ACCOUNT_MAP_KEY + botId + accountId;
        String conversationId = (String) this.redisTemplate.opsForHash().get(mapKey, "conversationId");
        if (conversationId != null && Boolean.TRUE.equals(this.redisTemplate.hasKey(conversationId))) {
            this.redisTemplate.delete(conversationId);
        }
    }

    @Override
    public void persistMessage(MessageMqTrans message) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setConversationId(message.getConversationId());
        messageEntity.setBotId(message.getBotId());
        messageEntity.setAccountId(message.getAccountId());
        messageEntity.setRole(message.getMessage().getRole());
        messageEntity.setContent(message.getMessage().getContent());
        this.messageMapper.insert(messageEntity);
    }

    private void sendPersistMessageMq(MessageMqTrans message) {
        this.redisTemplate.convertAndSend(MqConsts.MQ_CHAT_MESSAGE, message);
    }

    private String generateConversationId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void addMessage(String conversationId, String botId, String accountId, Message message) {
        this.redisTemplate.opsForList().rightPush(conversationId, message);
        this.redisTemplate.expire(conversationId, Duration.ofSeconds(EXPIRES));

        MessageMqTrans messageMqTrans = new MessageMqTrans();
        messageMqTrans.setConversationId(conversationId);
        messageMqTrans.setBotId(botId);
        messageMqTrans.setAccountId(accountId);
        messageMqTrans.setMessage(message);
        sendPersistMessageMq(messageMqTrans);
    }
}
