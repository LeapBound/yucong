package yzggy.yucong.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import yzggy.yucong.service.ConversationService;

import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@AllArgsConstructor
public class RedisListener implements MessageListener {

    private final ConversationService conversationService;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(pattern);
        List<Map<String, Object>> messageList = (List<Map<String, Object>>) this.redisTemplate.getValueSerializer().deserialize(message.getBody());
        this.conversationService.persistMessageMap(messageList);
        log.info("topic: {}, context: {}", topic, messageList);
    }
}
