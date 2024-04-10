package com.github.leapbound.yc.hub.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
    public List<MyMessage> completions(String botId, String accountId, List<MyMessage> messageList) {
        ProcessTaskDto task = this.actionServerService.queryNextTask(accountId);

        MyChatCompletionResponse response;
        switch (messageList.get(messageList.size() - 1).getType()) {
            case "image":
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
            case "video":
            default:
                response = sendToChatServer(botId, accountId, messageList, task);
        }
        List<MyMessage> gptMessageList = new ArrayList<>(2);

        // 处理function
        if (response.getMessage().getFunctionCall() != null) {
            // 执行function
            MyMessage message = this.funcService.invokeFunc(botId, accountId, response.getMessage().getFunctionCall());
            messageList.add(message);
            gptMessageList.add(message);

            response = getProcessTaskRemind(actionServerService.queryNextTask(accountId));
        } else if (task != null && task.getTaskId() != null) {
            response = getProcessTaskRemind(task);
        }

        // 助理消息
        MyMessage assistantMsg = new MyMessage();
        assistantMsg.setRole(response.getMessage().getRole());
        assistantMsg.setContent(response.getMessage().getContent());

        messageList.add(assistantMsg);
        gptMessageList.add(assistantMsg);

        return gptMessageList;
    }

    MyChatCompletionResponse getProcessTaskRemind(ProcessTaskDto task) {
        StringBuilder sb = new StringBuilder();
        if (task != null && task.getCurrentInputForm() != null) {
            sb.append("请提供以下信息:\n\n");
            task.getCurrentInputForm().forEach(input -> {
                if (!StringUtils.startsWithIgnoreCase(input.getId(), "z_")) {
                    switch (input.getType()) {
                        case "enum":
                            JSONObject config = actionServerService.loadProcessConfig(task.getProcessInstanceId());
                            if ("loanTerm".equals(input.getId())) {
                                sb.append(input.getLabel()).append("\n");
                                int i = 1;
                                for (Object stage : config.getJSONArray("StageCount")) {
                                    Map<String, Object> stageObject = (Map<String, Object>) stage;
                                    String value = stageObject.get("value").toString();
                                    if (!"请选择".equals(value)) {
                                        sb.append(i).append(". ").append(value).append("\n");
                                        i++;
                                    }
                                }
                            } else if ("maritalStatus".equals(input.getId())) {
                                sb.append(input.getLabel()).append("\n");
                                int i = 1;
                                for (Object stage : config.getJSONArray("Married")) {
                                    Map<String, Object> stageObject = (Map<String, Object>) stage;
                                    String value = stageObject.get("value").toString();
                                    if (!"请选择".equals(value)) {
                                        sb.append(i).append(". ").append(value).append("\n");
                                        i++;
                                    }
                                }
                            }
                            break;
                        default:
                            sb.append(input.getLabel()).append("\n");
                    }
                }
            });
        } else {
            sb.append("请稍等");
        }
        log.info("下一个任务需要的字段： \n{}", sb);

        MyMessage taskMessage = new MyMessage();
        taskMessage.setRole(MyMessage.Role.ASSISTANT.getName());
        taskMessage.setContent(sb.toString());
        MyChatCompletionResponse taskResponse = new MyChatCompletionResponse();
        taskResponse.setMessage(taskMessage);
        return taskResponse;
    }

    @Override
    public String summary(String content) {
        return this.openAiHandler.summary(content).getMessage().getContent();
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return this.openAiHandler.embedding(content);
    }

    private MyChatCompletionResponse sendToChatServer(String botId, String accountId, List<MyMessage> messageList, ProcessTaskDto task) {
        List<MyFunctions> functionsList = this.funcService.getListByAccountIdAndBotId(accountId, botId, task);
        return this.openAiHandler.chatCompletion(messageList, functionsList);
//        return this.qianfanHandler.chatCompletion(messageList, functionsList);
    }
}
