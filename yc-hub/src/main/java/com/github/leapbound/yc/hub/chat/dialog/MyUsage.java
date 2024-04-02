package com.github.leapbound.yc.hub.chat.dialog;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyUsage implements Serializable {

    private long promptTokens;
    private long completionTokens;
    private long totalTokens;
}
