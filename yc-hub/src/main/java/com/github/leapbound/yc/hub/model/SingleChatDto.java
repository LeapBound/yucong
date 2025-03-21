package com.github.leapbound.yc.hub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.leapbound.sdk.llm.chat.dialog.MyMessageType;
import lombok.Data;

import java.util.Map;

@Data
public class SingleChatDto {

    private String botId;
    private String accountId;
    private String content;
    private String picUrl;
    private MyMessageType type;
    private Map<String, Object> param;
}
