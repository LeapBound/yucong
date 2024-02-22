package com.github.leapbound.yc.hub.model;

import lombok.Data;

@Data
public class SingleChatModel {

    private String botId;
    private String accountId;
    private String content;
    private String picUrl;
    private String type;
}
