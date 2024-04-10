package com.github.leapbound.yc.hub.model;

import lombok.Data;

@Data
public class SingleChatDto {

    private String botId;
    private String accountId;
    private String content;
    private String picUrl;
    private String type;
}
