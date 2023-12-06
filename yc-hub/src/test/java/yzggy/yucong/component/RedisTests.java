package yzggy.yucong.component;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import yzggy.yucong.consts.MqConsts;

import java.util.List;

@Slf4j
@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    void mq() throws InterruptedException {
        int i = 0;
        while (i < 1) {
            i++;
            Message message = new Message();
            message.setRole("role" + i);
            message.setContent("content" + i);
            this.redisTemplate.convertAndSend(MqConsts.MQ_CHAT_MESSAGE, List.of(message));
            Thread.sleep(1000);
        }
    }
}
