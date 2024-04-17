package com.github.leapbound.yc.hub.service.impl.gpt.handler;

import com.github.leapbound.sdk.llm.chat.dialog.MyChatCompletionResponse;
import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;
import com.github.leapbound.sdk.llm.chat.dialog.MyUsage;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.sdk.llm.chat.func.MyFunctions;
import com.github.leapbound.sdk.llm.chat.func.MyParameters;
import com.github.leapbound.yc.hub.service.gpt.GptHandler;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.*;
import com.unfbx.chatgpt.entity.common.Usage;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("openaiHandler")
@RequiredArgsConstructor
public class OpenAiHandler implements GptHandler {

    private final OpenAiClient openAiClient;

    @Override
    public MyChatCompletionResponse chatCompletion(List<MyMessage> messageList, List<MyFunctions> functionsList) {
        ChatCompletion.ChatCompletionBuilder chatCompletionBuilder = ChatCompletion.builder()
                .messages(mapMyMessageListToMessageList(messageList))
                .model("gpt-3.5-turbo-1106");
        if (functionsList != null && !functionsList.isEmpty()) {
            chatCompletionBuilder
                    .functionCall("auto")
                    .functions(mapMyFunctionsListToFunctionsList(functionsList));
        }

        ChatCompletionResponse chatCompletionResponse = this.openAiClient.chatCompletion(chatCompletionBuilder.build());
        log.info("OpenAiHandler chatCompletion 返回结果: {}", chatCompletionResponse.getChoices().get(0));
        return mapChatCompletionResponseToMyChatCompletionResponse(chatCompletionResponse);
    }

    @Override
    public MyChatCompletionResponse summary(String content) {
        String template = "请对以下对话内容进行摘要\n" +
                "\n" +
                content;
        Message message = Message.builder()
                .role(Message.Role.USER)
                .content(template)
                .build();

        ChatCompletion.ChatCompletionBuilder chatCompletionBuilder = ChatCompletion.builder()
                .messages(List.of(message))
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName());

        ChatCompletionResponse chatCompletionResponse = this.openAiClient.chatCompletion(chatCompletionBuilder.build());
        log.info("OpenAiHandler summary 返回结果: {}", chatCompletionResponse.getChoices().get(0));
        return mapChatCompletionResponseToMyChatCompletionResponse(chatCompletionResponse);
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        EmbeddingResponse embeddingResponse = this.openAiClient.embeddings(content);
        return embeddingResponse.getData().get(0).getEmbedding();
    }

    private List<Message> mapMyMessageListToMessageList(List<MyMessage> messageList) {
        List<Message> list = new ArrayList<>(messageList.size());
        messageList.forEach(myMessage -> {
            Message message = Message.builder()
                    .name(myMessage.getName())
                    .role(myMessage.getRole())
                    .content(myMessage.getContent())
                    .build();

            if (myMessage.getFunctionCall() != null) {
                message.setFunctionCall(mapMyFunctionCallToFunctionCall(myMessage.getFunctionCall()));
            }

            list.add(message);
        });
        return list;
    }

    private List<Functions> mapMyFunctionsListToFunctionsList(List<MyFunctions> functionsList) {
        List<Functions> list = new ArrayList<>(functionsList.size());
        functionsList.forEach(myFunctions -> {
            Functions functions = Functions.builder()
                    .name(myFunctions.getName())
                    .description(myFunctions.getDescription())
                    .parameters(mapMyParametersToParameters(myFunctions.getParameters()))
                    .build();
            list.add(functions);
        });

        return list;
    }

    private FunctionCall mapMyFunctionCallToFunctionCall(MyFunctionCall myFunctionCall) {
        FunctionCall functionCall = new FunctionCall();
        functionCall.setName(myFunctionCall.getName());
        functionCall.setArguments(myFunctionCall.getArguments());
        return functionCall;
    }

    private Parameters mapMyParametersToParameters(MyParameters myParameters) {
        return Parameters.builder()
                .type(myParameters.getType())
                .properties(myParameters.getProperties())
                .required(myParameters.getRequired())
                .build();
    }

    private MyChatCompletionResponse mapChatCompletionResponseToMyChatCompletionResponse(ChatCompletionResponse chat) {
        MyChatCompletionResponse response = new MyChatCompletionResponse();
        response.setId(chat.getId());
        response.setObject(chat.getObject());
        response.setCreated(chat.getCreated());
        response.setModel(chat.getModel());
        response.setMessage(mapMessageToMyMessage(chat.getChoices().get(0).getMessage()));
        response.setUsage(mapUsageToMyUsage(chat.getUsage()));
        response.setWarning(chat.getWarning());

        return response;
    }

    private MyMessage mapMessageToMyMessage(Message message) {
        MyMessage myMessage = new MyMessage();
        myMessage.setName(message.getName());
        myMessage.setRole(message.getRole());
        myMessage.setContent(message.getContent());
        if (message.getFunctionCall() != null) {
            myMessage.setFunctionCall(mapFunctionCallToMyFunctionCall(message.getFunctionCall()));
        }
        return myMessage;
    }

    private MyFunctionCall mapFunctionCallToMyFunctionCall(FunctionCall functionCall) {
        MyFunctionCall myFunctionCall = new MyFunctionCall();
        myFunctionCall.setName(functionCall.getName());
        myFunctionCall.setArguments(functionCall.getArguments());
        return myFunctionCall;
    }

    private MyUsage mapUsageToMyUsage(Usage usage) {
        MyUsage myUsage = new MyUsage();
        myUsage.setPromptTokens(usage.getPromptTokens());
        myUsage.setCompletionTokens(usage.getCompletionTokens());
        myUsage.setTotalTokens(usage.getTotalTokens());
        return myUsage;
    }
}
