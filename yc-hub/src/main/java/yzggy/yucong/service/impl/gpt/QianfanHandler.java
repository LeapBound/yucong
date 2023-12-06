package yzggy.yucong.service.impl.gpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.vendor.bce.QianfanApiClient;
import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.dialog.MyUsage;
import yzggy.yucong.chat.func.MyFunctionCall;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.chat.func.MyParameters;
import yzggy.yucong.service.gpt.GptHandler;
import yzggy.yucong.vendor.bce.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class QianfanHandler implements GptHandler {

    private final QianfanApiClient qianfanApiClient;

    @Override
    public MyChatCompletionResponse chatCompletion(List<MyMessage> messageList, List<MyFunctions> functionsList) {
        ChatCompletion.ChatCompletionBuilder chatCompletionBuilder = ChatCompletion.builder()
                .stream(false);
        if (Objects.equals(messageList.get(0).getRole(), MyMessage.Role.SYSTEM.getName())) {
            chatCompletionBuilder.system(messageList.get(0).getContent());
            messageList = messageList.subList(1, messageList.size());
        }
        chatCompletionBuilder.messages(mapMyMessageListToMessageList(messageList));
        if (functionsList != null && !functionsList.isEmpty()) {
            chatCompletionBuilder
                    .functionCall("auto")
                    .functions(mapMyFunctionsListToFunctionsList(functionsList));
        }

        ChatCompletionResponse chatCompletionResponse = this.qianfanApiClient.chatCompletion(chatCompletionBuilder.build());
        log.info("QianfanHandler chatCompletion 返回结果: {}", chatCompletionResponse);
        return mapChatCompletionResponseToMyChatCompletionResponse(chatCompletionResponse);
    }

    @Override
    public MyChatCompletionResponse summary(String content) {
        return null;
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return null;
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
        response.setMessage(mapChatCompletionResponseToMyMessage(chat));
        if (chat.getUsage() != null) {
            response.setUsage(mapUsageToMyUsage(chat.getUsage()));
        }

        return response;
    }

    private MyMessage mapChatCompletionResponseToMyMessage(ChatCompletionResponse message) {
        MyMessage myMessage = new MyMessage();
        myMessage.setRole(Message.Role.ASSISTANT.getName());
        myMessage.setContent(message.getResult());
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
