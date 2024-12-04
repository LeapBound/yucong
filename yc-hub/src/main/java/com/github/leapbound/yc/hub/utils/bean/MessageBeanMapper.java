package com.github.leapbound.yc.hub.utils.bean;

import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;

/**
 * @author Fred Gu
 * @date 2024-12-04 17:17
 */
public class MessageBeanMapper {

    public static ChatMessage mapMyMessageToChatMessage(MyMessage myMessage) {
        ChatMessage chatMessage = new ChatMessage();
        switch (myMessage.getRole()) {
            case "system":
                chatMessage.setRole(ChatMessageRole.SYSTEM);
                break;
            case "user":
                chatMessage.setRole(ChatMessageRole.USER);
                break;
            case "assistant":
                chatMessage.setRole(ChatMessageRole.ASSISTANT);
                break;
            case "function":
                chatMessage.setRole(ChatMessageRole.FUNCTION);
                break;
            case "tool":
                chatMessage.setRole(ChatMessageRole.TOOL);
                break;
        }
        chatMessage.setContent(myMessage.getContent());
        return chatMessage;
    }

    public static MyMessage mapChatMessageToMyMessage(ChatMessage chatMessage) {
        MyMessage myMessage = new MyMessage();
        myMessage.setRole(chatMessage.getRole().value());
        myMessage.setContent((String) chatMessage.getContent());
        myMessage.setFunctionCall(FunctionBeanMapper.mapChatToolCallToMyFunctionCall(chatMessage.getToolCalls().get(0)));
        return myMessage;
    }
}