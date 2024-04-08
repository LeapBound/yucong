package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.yc.hub.service.ConversationService;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.github.leapbound.yc.hub.chat.dialog.MessageMqTrans;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.consts.MqConsts;
import com.github.leapbound.yc.hub.consts.RedisConsts;
import com.github.leapbound.yc.hub.entities.BotEntity;
import com.github.leapbound.yc.hub.entities.MessageEntity;
import com.github.leapbound.yc.hub.entities.MessageSummaryEntity;
import com.github.leapbound.yc.hub.mapper.BotMapper;
import com.github.leapbound.yc.hub.mapper.MessageMapper;
import com.github.leapbound.yc.hub.mapper.MessageSummaryMapper;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.gpt.GptService;
import com.github.leapbound.yc.hub.service.gpt.MilvusService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final BotMapper botMapper;
    private final MessageMapper messageMapper;
    private final MessageSummaryMapper messageSummaryMapper;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final GptService gptService;
    private final MilvusService milvusService;
    private final AmqpTemplate amqpTemplate;
    private final RestTemplate actionRestTemplate;

    private final ObjectMapper mapper;
    @Value("${yucong.conversation.expire:300}")
    private int expires;

    @Override
    public List<MyMessage> getByConversationId(String conversationId) {
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(conversationId))) {
            return null;
        }

        List<Object> objList = this.redisTemplate.opsForList().range(conversationId, 0, -1);
        List<MyMessage> messageList = new ArrayList<>(objList.size());
        objList.forEach(o -> messageList.add(mapper.convertValue(o, new TypeReference<>() {
        })));
        return messageList;
    }

    @Override
    public List<MyMessage> getByBotIdAndAccountId(String botId, String accountId) {
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + botId + accountId;
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
    public String chat(SingleChatDto singleChatModel) {
        String botId = singleChatModel.getBotId();
        String accountId = singleChatModel.getAccountId();
        String content = singleChatModel.getContent();

        List<MyMessage> messageList = getByBotIdAndAccountId(botId, accountId);
        if (messageList == null) {
            if (!start(botId, accountId)) {
                return "该bot没有调用权限";
            }
        }
        String conversationId = getConversationId(botId, accountId);

        // 匹配历史记忆
//        List<BigDecimal> embedding = this.gptService.embedding(content);
//        List<Float> floatList = new ArrayList<>(embedding.size());
//        embedding.forEach(item -> floatList.add(item.floatValue()));
//        String summaryConversationId = this.milvusService.search(floatList, 0.4);
//        if (StringUtils.hasText(summaryConversationId)) {
//            LambdaQueryWrapper<MessageSummaryEntity> summaryLQW = new LambdaQueryWrapper<MessageSummaryEntity>()
//                    .eq(MessageSummaryEntity::getConversationId, summaryConversationId)
//                    .last("limit 1");
//            MessageSummaryEntity summaryEntity = this.messageSummaryMapper.selectOne(summaryLQW);
//            MyMessage botMemory = new MyMessage();
//            botMemory.setRole(Message.Role.SYSTEM.getName());
//            botMemory.setContent(summaryEntity.getContent());
//            addMessage(conversationId, botId, accountId, botMemory);
//        }

        // 客户消息
        MyMessage userMsg = new MyMessage();
        userMsg.setRole(Message.Role.USER.getName());
        userMsg.setContent(content);
        userMsg.setPicUrl(singleChatModel.getPicUrl());
        userMsg.setType(singleChatModel.getType());
        addMessage(conversationId, botId, accountId, userMsg);

        // 调用gpt服务
        messageList = getByBotIdAndAccountId(botId, accountId);
        List<MyMessage> gptMessageList = this.gptService.completions(botId, accountId, messageList);
        gptMessageList.forEach(myMessage -> addMessage(conversationId, botId, accountId, myMessage));

        return gptMessageList.get(gptMessageList.size() - 1).getContent();
    }

    @Override
    public void summaryDialog(String conversationId) {
        String dialogContent = findContentOfDialogByConversationId(conversationId);

        if (StringUtils.hasText(dialogContent)) {
            String summaryContent = this.gptService.summary(dialogContent);
            MessageSummaryEntity messageSummaryEntity = new MessageSummaryEntity();
            messageSummaryEntity.setConversationId(conversationId);
            messageSummaryEntity.setContent(summaryContent);
            messageSummaryEntity.setCreateTime(new Date());
            this.messageSummaryMapper.insert(messageSummaryEntity);

            List<BigDecimal> embedding = this.gptService.embedding(dialogContent);
            List<Float> floatList = new ArrayList<>(embedding.size());
            embedding.forEach(item -> floatList.add(item.floatValue()));
            this.milvusService.insertData(conversationId, floatList);
        }
    }

    @Override
    public void clearMessageHistory(String botId, String accountId) {
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + botId + accountId;
        String conversationId = (String) this.redisTemplate.opsForHash().get(mapKey, "conversationId");
        if (conversationId != null && Boolean.TRUE.equals(this.redisTemplate.hasKey(conversationId))) {
            this.redisTemplate.delete(conversationId);
            this.redisTemplate.delete(RedisConsts.ACCOUNT_REMAIN_KEY);
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
        messageEntity.setCreateTime(message.getCreateTime());
        this.messageMapper.insert(messageEntity);
    }

    private String findContentOfDialogByConversationId(String conversationId) {
        LambdaQueryWrapper<MessageEntity> messageLQW = new LambdaQueryWrapper<MessageEntity>()
                .eq(MessageEntity::getConversationId, conversationId)
                .ne(MessageEntity::getRole, MyMessage.Role.SYSTEM.getName())
                .orderByAsc(MessageEntity::getId);
        List<MessageEntity> messageEntityList = this.messageMapper.selectList(messageLQW);

        if (!messageEntityList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            messageEntityList.forEach(messageEntity -> sb
                    .append(messageEntity.getRole())
                    .append(": ")
                    .append(messageEntity.getContent())
                    .append("\n"));
            log.info("summaryByConversationId: {}", sb);
            return sb.toString();
        }

        return null;
    }

    private void sendPersistMessageMq(MessageMqTrans message) {
        this.amqpTemplate.convertAndSend(MqConsts.MQ_DEFAULT_DIRECT_EXCHANGE, MqConsts.MQ_CHAT_MESSAGE_KEY, message);
    }

    private String generateConversationId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private Boolean start(String botId, String accountId) {
        // 系统消息，指定助理角色
        LambdaQueryWrapper<BotEntity> botLQW = new LambdaQueryWrapper<BotEntity>()
                .eq(BotEntity::getBotId, botId)
                .last("limit 1");
        BotEntity botEntity = this.botMapper.selectOne(botLQW);
        if (botEntity == null) {
            return false;
        }

        // 保留对话在redis中，并设置过期时间
        String conversationId = generateConversationId();
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + botId + accountId;
        this.redisTemplate.opsForHash().put(mapKey, "conversationId", conversationId);
        this.redisTemplate.expire(mapKey, Duration.ofSeconds(this.expires));

        // 保留对话key，供定时任务使用
        this.redisTemplate.opsForList().leftPush(RedisConsts.ACCOUNT_REMAIN_KEY, conversationId);

        // 初始化角色定义
        if (StringUtils.hasText(botEntity.getInitRoleContent())) {
            MyMessage systemMsg = new MyMessage();
            systemMsg.setRole(MyMessage.Role.SYSTEM.getName());
            systemMsg.setContent(botEntity.getInitRoleContent());
            addMessage(conversationId, botId, accountId, systemMsg);
        }

        return true;
    }

    private String getConversationId(String botId, String accountId) {
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + botId + accountId;
        this.redisTemplate.expire(mapKey, Duration.ofSeconds(this.expires));
        return (String) this.redisTemplate.opsForHash().get(mapKey, "conversationId");
    }

    private void addMessage(String conversationId, String botId, String accountId, MyMessage message) {
        if (StringUtils.hasText(message.getContent())
                || StringUtils.hasText(message.getPicUrl())) {
            this.redisTemplate.opsForList().rightPush(conversationId, message);
            this.redisTemplate.expire(conversationId, Duration.ofSeconds(this.expires));

            MessageMqTrans messageMqTrans = new MessageMqTrans();
            messageMqTrans.setConversationId(conversationId);
            messageMqTrans.setBotId(botId);
            messageMqTrans.setAccountId(accountId);
            messageMqTrans.setMessage(message);
            messageMqTrans.setCreateTime(new Date());
            sendPersistMessageMq(messageMqTrans);
        }
    }
}
