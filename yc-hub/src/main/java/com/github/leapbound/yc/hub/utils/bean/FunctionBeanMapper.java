package com.github.leapbound.yc.hub.utils.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.sdk.llm.chat.func.MyFunctions;
import com.volcengine.ark.runtime.model.completion.chat.ChatFunction;
import com.volcengine.ark.runtime.model.completion.chat.ChatFunctionCall;
import com.volcengine.ark.runtime.model.completion.chat.ChatToolCall;

/**
 * @author Fred Gu
 * @date 2024-12-04 16:21
 */
public class FunctionBeanMapper {

    public static ChatFunction mapMyFunctionToChatFunction(MyFunctions myFunctions) {

        ChatFunction chatFunction = new ChatFunction();
        chatFunction.setName(myFunctions.getName());
        chatFunction.setDescription(myFunctions.getDescription());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(myFunctions.getParameters());
        chatFunction.setParameters(jsonNode);

        return chatFunction;
    }

    public static MyFunctionCall mapChatFunctionCallToMyFunctionCall(ChatFunctionCall chatFunctionCall) {
        MyFunctionCall myFunctionCall = new MyFunctionCall();
        myFunctionCall.setName(chatFunctionCall.getName());
        myFunctionCall.setArguments(chatFunctionCall.getArguments());
        return myFunctionCall;
    }

    public static MyFunctionCall mapChatToolCallToMyFunctionCall(ChatToolCall chatToolCall) {
        return mapChatFunctionCallToMyFunctionCall(chatToolCall.getFunction());
    }
}
