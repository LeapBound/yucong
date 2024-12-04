package com.github.leapbound.component;

import com.github.leapbound.yc.hub.consts.RedisConsts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import com.github.leapbound.yc.hub.consts.MqConsts;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class RedisTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void mq() throws InterruptedException {
        int i = 0;
        while (i < 1) {
            i++;
            this.redisTemplate.convertAndSend(MqConsts.MQ_CHAT_MESSAGE, List.of(null));
            Thread.sleep(1000);
        }
    }

    @Test
    void addMap() {
        String conversationId = "generateConversationId";
        String mapKey = RedisConsts.ACCOUNT_MAP_KEY + "botId" + "accountId";
        this.redisTemplate.opsForHash().put(mapKey, "conversationId", conversationId);
        this.redisTemplate.opsForHash().put(mapKey, "dealWithAI", true);
        this.redisTemplate.expire(mapKey, Duration.ofSeconds(300));
    }
}
