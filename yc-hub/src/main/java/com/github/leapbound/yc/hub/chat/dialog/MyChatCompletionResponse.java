package com.github.leapbound.yc.hub.chat.dialog;

import lombok.Data;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;

import java.io.Serializable;

@Data
public class MyChatCompletionResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String result;
    private String model;
    private Boolean isTruncated;
    private Boolean needClearHistory;
    private MyMessage message;
    private MyFunctionCall functionCall;
    private MyUsage usage;
    private String warning;
}
