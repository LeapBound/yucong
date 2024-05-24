package com.github.leapbound.yc.hub.model.test;

import lombok.Data;

import java.util.Map;

/**
 * @author Fred
 * @date 2024/5/22 22:12
 */
@Data
public class TestMessageDto {

    private String type;
    private Boolean mock;
    private String content;
    private String picUrl;
    private String function;
    private Map<String, Object> functionParam;
    private Boolean needNotify;
}
