package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.sdk.llm.chat.dialog.MessageMqTrans;
import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.consts.MqConsts;
import com.github.leapbound.yc.hub.consts.RedisConsts;
import com.github.leapbound.yc.hub.entities.BotEntity;
import com.github.leapbound.yc.hub.entities.MessageEntity;
import com.github.leapbound.yc.hub.external.HubInteractiveService;
import com.github.leapbound.yc.hub.mapper.BotMapper;
import com.github.leapbound.yc.hub.mapper.MessageMapper;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final BotMapper botMapper;
    private final MessageMapper messageMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProcessService processService;
    private final AgentService agentService;
    private final HubInteractiveService hubInteractiveService;
    private final TimService timService;
    private final AmqpTemplate amqpTemplate;

    private final ObjectMapper mapper;
    @Value("${yucong.conversation.expire:300}")
    private int expires;
    private final Map<String, Boolean> notifyMap = new ConcurrentHashMap<>();

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
    public MyMessage chat(SingleChatDto singleChatModel) {
        return chat(singleChatModel, false);
    }

    @Override
    public MyMessage chat(SingleChatDto singleChatModel, Boolean isTest) {
        String botId = singleChatModel.getBotId();
        String accountId = singleChatModel.getAccountId();
        String content = singleChatModel.getContent();

        String conversationId = getConversationId(botId, accountId);
        if (!StringUtils.hasText(conversationId)) {
            if (!start(botId, accountId)) {
                MyMessage userMsg = new MyMessage();
                userMsg.setContent("该bot没有调用权限");
                return userMsg;
            } else {
                conversationId = getConversationId(botId, accountId);
            }
        }

        // 判断是对人还是对AI
        Boolean isDealWithAI = isDealWithAI(botId, accountId);
        if (isDealWithAI != null && isDealWithAI) {
            // 客户消息
            MyMessage userMsg = new MyMessage();
            userMsg.setRole(MyMessage.Role.USER.getName());
            switch (singleChatModel.getType()) {
//                case IMAGE, VIDEO:
//                    userMsg.setContent(singleChatModel.getType().getName());
//                    userMsg.setPicUrl(singleChatModel.getPicUrl());
//                    break;
                default:
                    userMsg.setContent(content);
            }
            userMsg.setType(singleChatModel.getType());
            addMessage(conversationId, botId, accountId, userMsg);

            // 调用agent服务
            List<MyMessage> messageList = getByConversationId(conversationId);
            List<MyMessage> gptMessageList = this.agentService.completions(botId, accountId, singleChatModel.getParam(), messageList, isTest);
            String finalConversationId = conversationId;
            gptMessageList.forEach(myMessage -> addMessage(finalConversationId, botId, accountId, myMessage));

            return gptMessageList.get(gptMessageList.size() - 1);
        } else {
//            this.timService.sendMsg(null, "test01", "tangxu", content);
            return new MyMessage();
        }
    }

    @Override
    public void notifyUser(SingleChatDto singleChatModel) {
        if (singleChatModel == null) {
            log.error("notifyUser singleChatModel is null");
            return;
        }

        String botId = singleChatModel.getBotId();
        String accountId = singleChatModel.getAccountId();
        String conversationId = getConversationId(botId, accountId);

        FunctionExecResultDto functionExecResultDto = new FunctionExecResultDto(true, null);
        String remind = this.processService.getProcessTaskRemind(singleChatModel.getAccountId(), null, functionExecResultDto);
        singleChatModel.setContent(remind);

        MyMessage assistantMsg = new MyMessage();
//        assistantMsg.setRole(Message.Role.ASSISTANT.getName());
        assistantMsg.setContent(remind);
        assistantMsg.setType(singleChatModel.getType());
        addMessage(conversationId, botId, accountId, assistantMsg);

        this.hubInteractiveService.receiveMsg(singleChatModel);

        this.notifyMap.put(accountId, true);
    }

    @Override
    public Boolean checkNotify(String accountId) {
        Boolean notify = this.notifyMap.get(accountId);
        this.notifyMap.remove(accountId);
        return notify;
    }

    @Override
    public void summaryDialog(String conversationId) {
        String dialogContent = findContentOfDialogByConversationId(conversationId);

        if (StringUtils.hasText(dialogContent)) {
//            String summaryContent = this.gptService.summary(dialogContent);
//            MessageSummaryEntity messageSummaryEntity = new MessageSummaryEntity();
//            messageSummaryEntity.setConversationId(conversationId);
//            messageSummaryEntity.setContent(summaryContent);
//            messageSummaryEntity.setCreateTime(new Date());
//            this.messageSummaryMapper.insert(messageSummaryEntity);
//
//            List<BigDecimal> embedding = this.gptService.embedding(dialogContent);
//            List<Float> floatList = new ArrayList<>(embedding.size());
//            embedding.forEach(item -> floatList.add(item.floatValue()));
//            this.milvusService.insertData(conversationId, floatList);
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
        this.redisTemplate.opsForHash().put(mapKey, "dealWithAI", true);
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

    private Boolean isDealWithAI(String botId, String accountId) {
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + botId + accountId;
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(mapKey))) {
            return null;
        }

        return (Boolean) this.redisTemplate.opsForHash().get(mapKey, "dealWithAI");
    }

    private String getConversationId(String botId, String accountId) {
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + botId + accountId;
        this.redisTemplate.expire(mapKey, Duration.ofSeconds(this.expires));
        return (String) this.redisTemplate.opsForHash().get(mapKey, "conversationId");
    }

    private void addMessage(String conversationId, String botId, String accountId, MyMessage message) {
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + botId + accountId;

        if ((StringUtils.hasText(message.getContent())
                || StringUtils.hasText(message.getPicUrl()))
                && Boolean.TRUE.equals(this.redisTemplate.hasKey(mapKey))) {
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
