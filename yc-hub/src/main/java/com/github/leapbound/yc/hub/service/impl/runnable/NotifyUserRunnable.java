package com.github.leapbound.yc.hub.service.impl.runnable;

import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fred
 * @date 2024/4/12 14:02
 */
@Slf4j
@RequiredArgsConstructor
public class NotifyUserRunnable implements Runnable {

    private final ConversationService conversationService;
    private final SingleChatDto singleChatModel;

    @Override
    public void run() {
        boolean execFlag = true;
        int times = 0;
        while (execFlag && times < 5) {
            times++;

            try {
                Thread.sleep(1000L * times);
                this.conversationService.notifyUser(singleChatModel);
                execFlag = false;
            } catch (Exception e) {
                log.info("NotifyUserRunnable [{}]", e.getMessage(), e);
            }
        }
    }
}
