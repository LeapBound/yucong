package yzggy.yucong.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.model.process.ProcessTaskDto;
import yzggy.yucong.service.gpt.FuncService;
import yzggy.yucong.service.gpt.GptHandler;
import yzggy.yucong.service.gpt.GptService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final FuncService funcService;
    private final GptHandler openAiHandler;
    private final GptHandler qianfanHandler;
    private final RestTemplate actionRestTemplate;

    @Override
    public List<MyMessage> completions(String botId, String accountId, List<MyMessage> messageList) {
        ProcessTaskDto task = queryNextTask(accountId);

        MyChatCompletionResponse response = sendToChatServer(botId, accountId, messageList, task);
        List<MyMessage> gptMessageList = new ArrayList<>(2);

        // 处理function
        if (response.getMessage().getFunctionCall() != null) {
            // 执行function
            MyMessage message = this.funcService.invokeFunc(botId, accountId, response.getMessage().getFunctionCall());
            messageList.add(message);
            gptMessageList.add(message);

            response = getProcessTaskRemind(queryNextTask(accountId));
        } else if (task != null) {
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
        StringBuilder sb = new StringBuilder("请提供以下信息:\n");
        task.getCurrentInputForm().forEach(input -> {
            if (!StringUtils.startsWithIgnoreCase(input.getId(), "z_")) {
                sb.append(input.getLabel()).append("\n");
            }
        });
        log.info("下一个任务需要的字段： {}", sb);

        MyMessage taskMessage = new MyMessage();
        taskMessage.setRole(MyMessage.Role.ASSISTANT.getName());
        taskMessage.setContent(sb.toString());
        MyChatCompletionResponse taskResponse = new MyChatCompletionResponse();
        taskResponse.setMessage(taskMessage);
        return taskResponse;
    }

    private ProcessTaskDto queryNextTask(String accountId) {
        // 查询是否存在进行中的流程
        ProcessTaskDto task = null;
        try {
            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("accountId", accountId);
            HttpEntity<String> requestEntity = new HttpEntity<>("", requestHeaders);
            ResponseEntity<ProcessTaskDto> entity = this.actionRestTemplate.exchange("/yc/business/task/next", HttpMethod.GET, requestEntity, ProcessTaskDto.class);

            task = entity.getBody();
        } catch (Exception e) {
            log.error("getTask error", e);
        }

        return task;
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
