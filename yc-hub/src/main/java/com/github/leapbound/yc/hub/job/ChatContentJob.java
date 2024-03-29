package com.github.leapbound.yc.hub.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.github.leapbound.yc.hub.consts.RedisConsts;
import com.github.leapbound.yc.hub.service.ConversationService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatContentJob {

    private final StringRedisTemplate redisTemplate;
    private final ConversationService conversationService;

//    @Scheduled(cron = "0/5 * * * * ?")
    public void summary() {
        List<String> conversationIdList = this.redisTemplate.opsForList().leftPop(RedisConsts.ACCOUNT_REMAIN_KEY, 10);
        if (conversationIdList != null && !conversationIdList.isEmpty()) {
            conversationIdList.forEach(conversationId -> {
                log.info("ChatContentJob summary {}", conversationId);
                this.conversationService.summaryDialog(conversationId);
            });

        }
    }
}
