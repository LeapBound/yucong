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
        while (execFlag) {
            try {
                Thread.sleep(1000);
                this.conversationService.notifyUser(singleChatModel);
                execFlag = false;
            } catch (Exception e) {
                log.info("NotifyUserRunnable [{}]", e.getMessage(), e);
            }
        }
    }
}
