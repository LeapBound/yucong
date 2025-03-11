package com.github.leapbound.yc.hub.utils.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.sdk.llm.chat.func.MyFunctions;
import com.github.leapbound.sdk.llm.chat.func.MyParameters;
import com.github.leapbound.yc.hub.entities.FunctionEntity;
import com.volcengine.ark.runtime.model.completion.chat.ChatFunction;
import com.volcengine.ark.runtime.model.completion.chat.ChatFunctionCall;
import com.volcengine.ark.runtime.model.completion.chat.ChatToolCall;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fred Gu
 * @date 2024-12-04 16:21
 */
@Slf4j
public class FunctionBeanMapper {

    public static MyFunctions mapFunctionEntityToMyFunction(FunctionEntity entity) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            MyFunctions myFunctions = new MyFunctions();
            myFunctions.setName(entity.getFunctionName());
            myFunctions.setParameters(mapper.readValue(entity.getFunctionParams(), MyParameters.class));
            myFunctions.setDescription(entity.getFunctionDescription());
            return myFunctions;
        } catch (JsonProcessingException e) {
            log.error("mapFunctionEntityToMyFunction error", e);
            return null;
        }
    }

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
