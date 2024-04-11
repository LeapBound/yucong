package com.github.leapbound.yc.hub.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.hub.chat.dialog.MyChatCompletionResponse;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.chat.func.MyFunctions;
import com.github.leapbound.yc.hub.consts.ProcessConsts;
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
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final FuncService funcService;
    private final ActionServerService actionServerService;

    private final GptHandler openAiHandler;
    private final GptHandler qianfanHandler;

    @Override
    public List<MyMessage> completions(String botId, String accountId, List<MyMessage> messageList) {
        ProcessTaskDto currentTask = this.actionServerService.queryNextTask(accountId);

        MyChatCompletionResponse response;
        switch (messageList.get(messageList.size() - 1).getType()) {
            case "image":
            case "video":
                MyMessage inMessage = messageList.get(messageList.size() - 1);

                MyFunctionCall myFunctionCall = new MyFunctionCall();
                Map<String, String> args = new HashMap<>();
                args.put("imgUrl", inMessage.getPicUrl());
                myFunctionCall.setArguments(JSON.toJSONString(args));

                MyMessage outMessage = new MyMessage();
                outMessage.setFunctionCall(myFunctionCall);

                response = new MyChatCompletionResponse();
                response.setMessage(outMessage);
                break;
            case "text":
            default:
                response = sendToChatServer(botId, accountId, messageList, currentTask);
        }
        List<MyMessage> gptMessageList = new ArrayList<>(2);

        // 处理function
        Boolean functionExecuteResult = null;
        if (response.getMessage().getFunctionCall() != null) {
            // 执行function
            functionExecuteResult = this.funcService.invokeFunc(botId, accountId, response.getMessage().getFunctionCall());
        }
        String remind = getProcessTaskRemind(accountId, currentTask, functionExecuteResult);
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

    String getProcessTaskRemind(String accountId, ProcessTaskDto currentTask, Boolean functionExecuteResult) {
        if (functionExecuteResult != null) {
            // 用户触发了开始流程前的function，currentTask为空
            if (currentTask == null) {
                return getTaskProperty(this.actionServerService.queryNextTask(accountId), ProcessConsts.TASK_REMIND_BEFORE);
            }

            // 触发了流程中的function
            if (functionExecuteResult) {
                String afterRemindSuccess = getTaskProperty(currentTask, ProcessConsts.TASK_REMIND_AFTER_SUCCESS);
                // 判断当前task是否有结束提醒
                if (StringUtils.hasText(afterRemindSuccess)) {
                    return afterRemindSuccess;
                } else {
                    ProcessTaskDto nextTask = this.actionServerService.queryNextTask(accountId);
                    String beforeRemind = getTaskProperty(nextTask, ProcessConsts.TASK_REMIND_BEFORE);

                    String showVariable = getTaskProperty(nextTask, ProcessConsts.TASK_SHOW_VARIABLE);
                    if (StringUtils.hasText(showVariable)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(beforeRemind).append(":\n\n");

                        JSONObject variables = this.actionServerService.loadProcessVariables(nextTask.getProcessInstanceId());
                        int i = 1;
                        for (Object stage : variables.getJSONArray(showVariable)) {
                            String stageObject = (String) stage;
                            if (!"请选择".equals(stageObject)) {
                                sb.append(i).append(". ").append(stageObject).append("\n");
                                i++;
                            }
                        }

                        return sb.toString();
                    }

                    return beforeRemind;
                }
            } else {
                return getTaskProperty(currentTask, ProcessConsts.TASK_REMIND_AFTER_FAIL);
            }
        } else {
            if (currentTask == null) {
                // 没有执行function，没有需要完成的task
                return null;
            } else {
                // 没有执行function，但是有需要完成的task
                return null;
            }
        }
    }

    private String getTaskProperty(ProcessTaskDto task, String name) {
        AtomicReference<String> type = new AtomicReference<>();

        task.getTaskProperties().stream()
                .filter(property -> {
                    String propertyName = (String) property.get("name");
                    return propertyName.equals(name);
                })
                .findFirst()
                .ifPresent(property -> type.set((String) property.get("type")));

        return type.get();
    }

    @Override
    public String summary(String content) {
        return getHandler().summary(content).getMessage().getContent();
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return getHandler().embedding(content);
    }

    private MyChatCompletionResponse sendToChatServer(String botId, String accountId, List<MyMessage> messageList, ProcessTaskDto task) {
        List<MyFunctions> functionsList = this.funcService.getListByAccountIdAndBotId(accountId, botId, task);
        return getHandler().chatCompletion(messageList, functionsList);
//        return getHandler().chatCompletion(messageList, functionsList);
    }

    private GptHandler getHandler() {
        return this.openAiHandler;
    }
}
