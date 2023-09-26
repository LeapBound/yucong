package yzggy.yucong.chat.dialog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import yzggy.yucong.chat.func.MyFunctionCall;

@Data
public class MyMessage {

    private String role;
    private String content;
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
