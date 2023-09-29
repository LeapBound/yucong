package yzggy.yucong.bce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

    private String role;
    private String content;
    private String name;

    @JsonProperty("function_call")
    private FunctionCall functionCall;

    public static Message.Builder builder() {
        return new Message.Builder();
    }

    /**
     * 构造函数
     *
     * @param role         角色
     * @param content      描述主题信息
     * @param name         name
     * @param functionCall functionCall
     */
    public Message(String role, String content, String name, FunctionCall functionCall) {
        this.role = role;
        this.content = content;
        this.name = name;
        this.functionCall = functionCall;
    }

    public Message() {
    }

    private Message(Message.Builder builder) {
        setRole(builder.role);
        setContent(builder.content);
        setName(builder.name);
        setFunctionCall(builder.functionCall);
    }

    @Getter
    @AllArgsConstructor
    public enum Role {

        USER("user"),
        ASSISTANT("assistant"),
        FUNCTION("function");

        private final String name;
    }

    public static final class Builder {
        private String role;
        private String content;
        private String name;
        private FunctionCall functionCall;

        public Builder() {
        }

        public Message.Builder role(com.unfbx.chatgpt.entity.chat.Message.Role role) {
            this.role = role.getName();
            return this;
        }

        public Message.Builder role(String role) {
            this.role = role;
            return this;
        }

        public Message.Builder content(String content) {
            this.content = content;
            return this;
        }

        public Message.Builder name(String name) {
            this.name = name;
            return this;
        }

        public Message.Builder functionCall(FunctionCall functionCall) {
            this.functionCall = functionCall;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

}
