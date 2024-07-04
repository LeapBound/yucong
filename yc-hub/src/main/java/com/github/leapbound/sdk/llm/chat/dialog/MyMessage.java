package com.github.leapbound.sdk.llm.chat.dialog;

import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.chat.dialog.MyMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Data
public class MyMessage implements Serializable {

    private String role;
    private String content;
    private String picUrl;
    private MyMessageType type;
    private String name;
    private Map<String, Object> param;

    private MyFunctionCall functionCall;

    @Getter
    @AllArgsConstructor
    public enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        FUNCTION("function");

        private final String name;
    }

}
