package com.github.leapbound.yc.hub.controller.api;

import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;
import com.github.leapbound.sdk.llm.chat.dialog.MyMessageType;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.model.test.TestFlowDto;
import com.github.leapbound.yc.hub.model.test.TestMessageDto;
import com.github.leapbound.yc.hub.service.ConversationService;
import com.github.leapbound.yc.hub.service.ProcessService;
import com.github.leapbound.yc.hub.vendor.wx.cp.YcWxCpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.constant.WxCpConsts;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

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
    private final ProcessService processService;

    private final YcWxCpService ycWxCpService;

    @PostMapping("/flow")
    public String testFlow(@RequestBody TestFlowDto testFlowDto) {
        String botId = testFlowDto.getBotId();
        String accountId = testFlowDto.getAccountId();

        // 清理历史聊天记录
        this.conversationService.clearMessageHistory(botId, accountId);

        // 删除现有流程
        deleteProcess(accountId);

        // 判断渠道，运行流程
        switch (testFlowDto.getChannel()) {
            case "wxCpKf":
                wxKf(testFlowDto);
                break;
            case "wxCp":
                break;
            default:
                defaultChannel(testFlowDto);
        }

        // 打印并返回聊天记录
        return printChatLogsAndReturn(botId, accountId);
    }

    private void wxKf(TestFlowDto testFlowDto) {
        /*
         * 微信客服进来时，知道几个信息
         * 1. openKfId
         * 2. corpId
         * 3. agentId
         * 用户的externalUserId是需要根据openKfId去微信拉取的
         */
        String corpId = testFlowDto.getCorpId();
        Integer agentId = testFlowDto.getAgentId();
        String openKfId = testFlowDto.getOpenKfId();
        String externalUserId = testFlowDto.getExternalId();
        String accountId = testFlowDto.getAccountId();

        final WxCpService wxCpService = this.ycWxCpService.getCpService(corpId, agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应agentId=[%d]的配置，请核实！", agentId));
        }
        WxCpMessageRouter wxCpMessageRouter = this.ycWxCpService.getCpRouter(corpId, agentId);

        for (TestMessageDto message : testFlowDto.getMessages()) {
            log.info("*".repeat(100));
            log.info("user content: {}", message.getContent());

            WxCpXmlMessage inMessage = new WxCpXmlMessage();
            inMessage.setMsgType("yc_test_event");
            inMessage.setMsgId(new Random().nextLong());
            inMessage.setOpenKfId(openKfId);
            inMessage.setExternalUserId(externalUserId);
            inMessage.setContent(message.getContent());
            inMessage.setEvent(WxCpConsts.EventType.KF_MSG_OR_EVENT);

            if (message.getMock() == null || message.getMock()) {
                WxCpXmlMessage.ExtAttr extAttr = new WxCpXmlMessage.ExtAttr();
                WxCpXmlMessage.ExtAttr.Item item = new WxCpXmlMessage.ExtAttr.Item();
                item.setName("mock");
                extAttr.getItems().add(item);
                inMessage.setExtAttrs(extAttr);

                if (StringUtils.hasText(message.getFunction())) {
                    MyFunctionCall functionCall = MyFunctionCall.builder()
                            .name(message.getFunction())
                            .arguments(null)
                            .build();
                }
            }

            wxCpMessageRouter.route(inMessage);

            // 检查action server是否通知完成
            if (message.getNeedNotify() != null && message.getNeedNotify()) {
                checkTaskNotified(accountId);
            }
        }
    }

    private void defaultChannel(TestFlowDto testFlowDto) {
        String botId = testFlowDto.getBotId();
        String accountId = testFlowDto.getAccountId();

        SingleChatDto singleChatDto = new SingleChatDto();
        singleChatDto.setBotId(botId);
        singleChatDto.setAccountId(accountId);
        for (TestMessageDto message : testFlowDto.getMessages()) {
            log.info("*".repeat(100));
            log.info("user content: {}", message.getContent());

            singleChatDto.setContent(message.getContent());
            if (message.getType() != null) {
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
                            .arguments(null)
                            .build();
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
    }

    private void deleteProcess(String accountId) {
        ProcessTaskDto processTaskDto = this.processService.queryNextTask(accountId);
        if (processTaskDto != null && processTaskDto.getProcessInstanceId() != null) {
            this.processService.deleteProcess(processTaskDto.getProcessInstanceId());
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

    private String printChatLogsAndReturn(String botId, String accountId) {
        StringBuilder sb = new StringBuilder();
        List<MyMessage> messageList = this.conversationService.getByBotIdAndAccountId(botId, accountId);
        if (messageList != null) {
            log.info("#".repeat(100));
            messageList.forEach(message -> {
                String line = String.format("%-9s %s", message.getRole(), message.getContent());
                sb.append(line).append("\n");
                log.info(line);
            });
        }

        return sb.toString();
    }

}
