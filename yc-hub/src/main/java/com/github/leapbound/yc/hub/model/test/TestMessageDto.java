package com.github.leapbound.yc.hub.model.test;

import com.github.leapbound.yc.hub.chat.dialog.MyMessageType;
import lombok.Data;

import java.util.Map;

/**
 * @author Fred
 * @date 2024/5/22 22:12
 */
@Data
public class TestMessageDto {

    private MyMessageType type;
    private Boolean mock;
    private String content;
    private String picUrl;
    private String function;
    private Map<String, Object> functionParam;
    private Boolean needNotify;
}
