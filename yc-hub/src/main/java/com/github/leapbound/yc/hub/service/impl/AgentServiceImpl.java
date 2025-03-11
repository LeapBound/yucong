package com.github.leapbound.yc.hub.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;
import com.github.leapbound.sdk.llm.chat.dialog.MyMessageType;
import com.github.leapbound.sdk.llm.chat.func.MyFunctions;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.AgentService;
import com.github.leapbound.yc.hub.service.FuncService;
import com.github.leapbound.yc.hub.service.YcProcessService;
import com.github.leapbound.yc.hub.utils.bean.FunctionBeanMapper;
import com.github.leapbound.yc.hub.utils.bean.MessageBeanMapper;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatTool;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Fred Gu
 * @date 2024-12-04 11:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final YcProcessService ycProcessService;
    private final FuncService funcService;
    private final ArkService arkService;

    @Value("${yucong.llm.doubao.endpointId}")
    private String endpointId;

    @Override
    public List<MyMessage> completions(String botId, String accountId, Map<String, Object> params, List<MyMessage> messageList, Boolean isTest) {
        ProcessTaskDto currentTask = this.ycProcessService.queryNextTask(accountId);

        MyMessage response;
        switch (messageList.get(messageList.size() - 1).getType()) {
//            case IMAGE, VIDEO:
//                response = processImg(botId, accountId, messageList.get(messageList.size() - 1), currentTask);
//                break;
            case TEXT:
            default:
                response = sendToChatServer(botId, accountId, messageList, currentTask, isTest);
        }
        List<MyMessage> gptMessageList = new ArrayList<>(2);

        // 入参
        if (currentTask != null && params != null && !params.isEmpty()) {
            this.ycProcessService.inputProcessVariable(currentTask.getProcessInstanceId(), accountId, params);
        }

        // 处理function
        FunctionExecResultDto functionExecuteResult = null;
        if (response.getFunctionCall() != null) {
            // 执行function
            functionExecuteResult = this.funcService.invokeFunc(botId, accountId, response.getFunctionCall());

            // 根据任务状态完成任务
            if (currentTask != null) {
                // todo
                this.ycProcessService.completeTask(currentTask.getTaskId(), (Map<String, Object>) JSON.parse(response.getFunctionCall().getArguments()));
            }
        }
        String remind = this.ycProcessService.getProcessTaskRemind(accountId, currentTask, functionExecuteResult);
        log.debug("remind {}", remind);

        // 助理消息
        MyMessage assistantMsg = new MyMessage();
        assistantMsg.setRole(response.getRole());
        assistantMsg.setType(MyMessageType.TEXT);
        if (StringUtils.hasText(remind)) {
            assistantMsg.setContent(remind);
        } else if (functionExecuteResult != null && StringUtils.hasText(functionExecuteResult.getMsg())) {
            assistantMsg.setContent(functionExecuteResult.getMsg());
        } else {
            assistantMsg.setContent(response.getContent());
        }

        gptMessageList.add(assistantMsg);
        return gptMessageList;
    }

//    private MyChatCompletionResponse processImg(String botId, String accountId, MyMessage inMessage, ProcessTaskDto currentTask) {
//        String id = currentTask.getCurrentInputForm().get(0).getId();
//        MyFunctions functions = this.funcService.getListByAccountIdAndBotId(accountId, botId, currentTask).get(0);
//
//        MyFunctionCall myFunctionCall = new MyFunctionCall();
//        myFunctionCall.setName(functions.getName());
//        Map<String, String> args = new HashMap<>();
//        args.put(id, inMessage.getPicUrl());
//        myFunctionCall.setArguments(JSON.toJSONString(args));
//
//        MyMessage outMessage = new MyMessage();
//        outMessage.setRole(MyMessage.Role.ASSISTANT.getName());
//        outMessage.setFunctionCall(myFunctionCall);
//        outMessage.setRole(Message.Role.ASSISTANT.getName());
//
//        MyChatCompletionResponse response = new MyChatCompletionResponse();
//        response.setMessage(outMessage);
//        return response;
//    }

//    @Override
//    public String summary(String content) {
//        return getHandler().summary(content).getMessage().getContent();
//    }

//    @Override
//    public List<BigDecimal> embedding(String content) {
//        return getHandler().embedding(content);
//    }

    private MyMessage sendToChatServer(String botId, String accountId, List<MyMessage> messageList, ProcessTaskDto currentTask, Boolean isTest) {
        List<ChatMessage> chatMessageList = messageList.stream()
                .map(MessageBeanMapper::mapMyMessageToChatMessage)
                .collect(Collectors.toCollection(ArrayList::new));

        List<MyFunctions> functionsList = this.funcService.getListByAccountIdAndBotId(accountId, botId, currentTask);
        List<ChatTool> chatToolList = functionsList.stream()
                .map(f -> new ChatTool("function", FunctionBeanMapper.mapMyFunctionToChatFunction(f)))
                .collect(Collectors.toCollection(ArrayList::new));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(this.endpointId)
                .messages(chatMessageList)
                .tools(chatToolList)
                .build();
        log.debug("sendToChatServer chatCompletionRequest: {}", chatCompletionRequest);

        ChatCompletionChoice chatCompletionChoice = this.arkService.createChatCompletion(chatCompletionRequest).getChoices().get(0);
        log.debug("sendToChatServer chatCompletionChoice: {}", chatCompletionChoice);
        return MessageBeanMapper.mapChatMessageToMyMessage(chatCompletionChoice.getMessage());
    }
}
