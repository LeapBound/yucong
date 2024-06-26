package com.github.leapbound.yc.hub.controller.api;

import com.alibaba.fastjson.JSON;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.dialog.MyMessageType;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.model.test.TestFlowDto;
import com.github.leapbound.yc.hub.model.test.TestMessageDto;
import com.github.leapbound.yc.hub.service.ActionServerService;
import com.github.leapbound.yc.hub.service.ConversationService;
import com.github.leapbound.yc.hub.service.gpt.GptMockHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Fred
 * @date 2024/5/22 18:00
 */
@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/api/test")
@RequiredArgsConstructor
public class ApiTestController {

    private final ConversationService conversationService;
    private final ActionServerService actionServerService;

    private final GptMockHandler mockHandler;

    @PostMapping("/flow")
    public String testFlow(@RequestBody TestFlowDto testFlowDto) {
        // 清理历史聊天记录
        String botId = testFlowDto.getChat().getBotId();
        String accountId = testFlowDto.getChat().getAccountId();
        this.conversationService.clearMessageHistory(botId, accountId);

        // 删除现有流程
        deleteProcess(accountId);

        // 运行流程
        SingleChatDto singleChatDto = testFlowDto.getChat();
        for (TestMessageDto message : testFlowDto.getMessages()) {
            log.info("*".repeat(100));
            log.info("user content: {}", message.getContent());

            singleChatDto.setContent(message.getContent());
            if (StringUtils.hasText(String.valueOf(message.getType()))) {
                singleChatDto.setType(message.getType());
            } else {
                singleChatDto.setType(MyMessageType.TEXT);
            }
            if (StringUtils.hasText(message.getPicUrl())) {
                singleChatDto.setPicUrl(message.getPicUrl());
            }

            if (message.getMock() == null || message.getMock()) {
                if (StringUtils.hasText(message.getFunction())) {
                    MyFunctionCall functionCall = MyFunctionCall.builder()
                            .name(message.getFunction())
                            .arguments(message.getFunctionParam() == null ? "{}" : JSON.toJSONString(message.getFunctionParam()))
                            .build();
                    this.mockHandler.setFunctionCall(functionCall);
                }
                this.conversationService.chat(singleChatDto, true);
            } else {
                this.conversationService.chat(singleChatDto);
            }

            // todo
            // this.conversationService.notifyUser(singleChatDto);

            // 检查action server是否通知完成
            if (message.getNeedNotify() != null && message.getNeedNotify()) {
                checkTaskNotified(accountId);
            }
        }

        // 打印聊天记录
        List<MyMessage> messageList = this.conversationService.getByBotIdAndAccountId(botId, accountId);
        if (messageList != null) {
            log.info("#".repeat(100));
            messageList.forEach(message ->
                    log.info(String.format("%-9s %s", message.getRole(), message.getContent()))
            );
        }
        return null;
    }

    private void deleteProcess(String accountId) {
        ProcessTaskDto processTaskDto = this.actionServerService.queryNextTask(accountId);
        if (processTaskDto != null && processTaskDto.getProcessInstanceId() != null) {
            this.actionServerService.deleteProcess(processTaskDto.getProcessInstanceId());
        }
    }

    private void checkTaskNotified(String accountId) {
        Boolean notifyResult = null;
        while (notifyResult == null) {
            try {
                log.info("checkTaskNotified");
                Thread.sleep(1000);
                notifyResult = this.conversationService.checkNotify(accountId);
            } catch (InterruptedException e) {
                log.error("checkTaskNotified", e);
            }
        }
    }
}
