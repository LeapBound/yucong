package com.github.leapbound.yc.hub.model;

import lombok.Data;

import java.util.Date;

@Data
public class BotDto {

    private String botId;
    private String botName;
    private String initContent;
    private Date createTime;
}
