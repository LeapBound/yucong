package com.github.leapbound.yc.hub.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.github.leapbound.yc.hub.chat.dialog.MessageMqTrans;
import com.github.leapbound.yc.hub.consts.MqConsts;
import com.github.leapbound.yc.hub.service.ConversationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageDirectReceiver {

    private final ConversationService conversationService;

    @RabbitListener(queues = MqConsts.MQ_CHAT_MESSAGE)
    public void receive(MessageMqTrans chatMessage) {
//        log.info("接收到{}的消息：{}", MqConsts.MQ_CHAT_MESSAGE, chatMessage);
        this.conversationService.persistMessage(chatMessage);
    }
}
