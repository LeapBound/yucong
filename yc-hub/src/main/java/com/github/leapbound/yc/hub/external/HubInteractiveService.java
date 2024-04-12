package com.github.leapbound.yc.hub.external;

import com.github.leapbound.yc.hub.model.SingleChatDto;

/**
 * @author Fred
 * @date 2024/4/11 16:33
 */
public interface HubInteractiveService {

    void receiveMsg(SingleChatDto singleChatDto);
}
