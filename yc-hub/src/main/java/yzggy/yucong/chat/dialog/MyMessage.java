package yzggy.yucong.chat.dialog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import yzggy.yucong.chat.func.MyFunctionCall;

import java.io.Serializable;

@Data
public class MyMessage implements Serializable {

    private String role;
    private String content;
    private String picUrl;
    private String type;
    private String name;

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
