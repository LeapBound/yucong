package com.github.leapbound.yc.hub.model.test;

import com.github.leapbound.yc.hub.model.SingleChatDto;
import lombok.Data;

import java.util.List;

/**
 * @author Fred
 * @date 2024/5/22 18:03
 */
@Data
public class TestFlowDto {
    private String channel;

    private SingleChatDto chat;
    private List<TestMessageDto> messages;
}
