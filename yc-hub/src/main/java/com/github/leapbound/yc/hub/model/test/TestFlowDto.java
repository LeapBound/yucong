package com.github.leapbound.yc.hub.model.test;

import lombok.Data;

import java.util.List;

/**
 * @author Fred
 * @date 2024/5/22 18:03
 */
@Data
public class TestFlowDto {
    private String channel;

    private String botId;
    private String accountId;

    private String corpId;
    private Integer agentId;
    private String openKfId;
    private String externalId;

    private List<TestMessageDto> messages;
}
