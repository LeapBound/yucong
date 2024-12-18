package com.github.leapbound.yc.hub.vendor.wx.cp.handler;

import com.github.leapbound.sdk.llm.chat.dialog.MyMessageType;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.BotService;
import com.github.leapbound.yc.hub.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpMessageServiceImpl;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CpMsgHandler extends AbstractHandler {

    private final ConversationService conversationService;
    private final BotService botService;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> map, WxCpService wxCpService, WxSessionManager wxSessionManager) throws WxErrorException {
        log.debug("CpMsgHandler 接收到请求消息，WxCpXmlMessage：{} map: {}", wxMessage, map);

        String username = wxMessage.getFromUserName();
        String content = wxMessage.getContent();
        log.info("CpMsgHandler 接收到请求消息 username: {} content: {}", username, content);

        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botService.getBotId(wxCpService.getWxCpConfigStorage().getCorpId(), wxMessage.getAgentId()));
        singleChatModel.setAccountId(username);
        singleChatModel.setContent(content);
        singleChatModel.setType(MyMessageType.TEXT);
        String msg = this.conversationService.chat(singleChatModel).getContent();

        if (StringUtils.hasText(msg)) {
            try {
                WxCpMessage message = WxCpMessage
                        .TEXT()
                        .agentId(Integer.valueOf(wxMessage.getAgentId()))
                        .toUser(username)
                        .content(msg)
                        .build();

                WxCpMessageServiceImpl wxCpMessageService = new WxCpMessageServiceImpl(wxCpService);
                wxCpMessageService.send(message);
            } catch (WxErrorException e) {
                log.error("CpMsgHandler send error", e);
            }
        }

        return null;
    }
}