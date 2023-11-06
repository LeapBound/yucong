package yzggy.yucong.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import yzggy.yucong.chat.dialog.MessageMqTrans;
import yzggy.yucong.service.ConversationService;

@Slf4j
@Configuration
@AllArgsConstructor
public class RedisListener implements MessageListener {

    private final ConversationService conversationService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(pattern);
        MessageMqTrans chatMessage = null;
        try {
            chatMessage = this.objectMapper.readValue(new String(message.getBody()), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("onMessage error", e);
        }

        this.conversationService.persistMessage(chatMessage);
        log.debug("topic: {}, context: {}", topic, chatMessage);
    }
}
