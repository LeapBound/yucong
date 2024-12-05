package com.github.leapbound.sdk.llm.chat.dialog;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageMqTrans implements Serializable {

    private String conversationId;
    private String botId;
    private String accountId;
    private MyMessage message;
    private Date createTime;
}
