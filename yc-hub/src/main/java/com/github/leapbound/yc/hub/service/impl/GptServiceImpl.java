package com.github.leapbound.yc.hub.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.leapbound.yc.hub.chat.dialog.MyChatCompletionResponse;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.chat.func.MyFunctions;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.ActionServerService;
import com.github.leapbound.yc.hub.service.gpt.FuncService;
import com.github.leapbound.yc.hub.service.gpt.GptHandler;
import com.github.leapbound.yc.hub.service.gpt.GptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final FuncService funcService;
    private final ActionServerService actionServerService;

    private final GptHandler openAiHandler;
    private final GptHandler qianfanHandler;

    @Override
    public List<MyMessage> completions(String botId, String accountId, Map<String, Object> params, List<MyMessage> messageList) {
        ProcessTaskDto currentTask = this.actionServerService.queryNextTask(accountId);

        MyChatCompletionResponse response;
        switch (messageList.get(messageList.size() - 1).getType()) {
            case "image":
            case "video":
                response = processImg(botId, accountId, messageList.get(messageList.size() - 1), currentTask);
                break;
            case "text":
            default:
                response = sendToChatServer(botId, accountId, messageList, currentTask);
        }
        List<MyMessage> gptMessageList = new ArrayList<>(2);

        // 入参
        if (params != null && !params.isEmpty()) {
            this.actionServerService.inputProcessVariable(currentTask.getProcessInstanceId(), accountId, params);
        }

        // 处理function
        Boolean functionExecuteResult = null;
        if (response.getMessage().getFunctionCall() != null) {
            // 执行function
            functionExecuteResult = this.funcService.invokeFunc(botId, accountId, response.getMessage().getFunctionCall());
        }
        String remind = this.actionServerService.getProcessTaskRemind(accountId, currentTask, functionExecuteResult);
        log.debug("remind {}", remind);

        // 助理消息
        MyMessage assistantMsg = new MyMessage();
        assistantMsg.setRole(response.getMessage().getRole());
        if (StringUtils.hasText(remind)) {
            assistantMsg.setContent(remind);
        } else {
            assistantMsg.setContent(response.getMessage().getContent());
        }

        gptMessageList.add(assistantMsg);
        return gptMessageList;
    }

    private MyChatCompletionResponse processImg(String botId, String accountId, MyMessage inMessage, ProcessTaskDto currentTask) {
        String id = currentTask.getCurrentInputForm().get(0).getId();
        MyFunctions functions = this.funcService.getListByAccountIdAndBotId(accountId, botId, currentTask).get(0);

        MyFunctionCall myFunctionCall = new MyFunctionCall();
        myFunctionCall.setName(functions.getName());
        Map<String, String> args = new HashMap<>();
        args.put(id, inMessage.getPicUrl());
        myFunctionCall.setArguments(JSON.toJSONString(args));

        MyMessage outMessage = new MyMessage();
        outMessage.setRole(MyMessage.Role.ASSISTANT.getName());
        outMessage.setFunctionCall(myFunctionCall);

        MyChatCompletionResponse response = new MyChatCompletionResponse();
        response.setMessage(outMessage);
        return response;
    }

    @Override
    public String summary(String content) {
        return getHandler().summary(content).getMessage().getContent();
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return getHandler().embedding(content);
    }

    private MyChatCompletionResponse sendToChatServer(String botId, String accountId, List<MyMessage> messageList, ProcessTaskDto currentTask) {
        List<MyFunctions> functionsList = this.funcService.getListByAccountIdAndBotId(accountId, botId, currentTask);
        return getHandler().chatCompletion(messageList, functionsList);
//        return getHandler().chatCompletion(messageList, functionsList);
    }

    private GptHandler getHandler() {
        return this.openAiHandler;
    }
}
