package com.github.leapbound.yc.hub.vendor.wx.mp;

import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.BotService;
import com.github.leapbound.yc.hub.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpMsgHandler extends AbstractHandler {

    private final ConversationService conversationService;
    private final BotService botService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        log.debug("MpMsgHandler 接收到请求消息，WxCpXmlMessage：{} map: {}", wxMessage, map);

        String username = wxMessage.getFromUser();
        String content = wxMessage.getContent();
        log.info("MpMsgHandler 接收到请求消息 username: {} content: {}", username, content);

        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(this.botService.getBotId(wxMpService.getWxMpConfigStorage().getAppId()));
        singleChatModel.setAccountId(username);
        singleChatModel.setContent(content);
        singleChatModel.setPicUrl(wxMessage.getPicUrl());
        String msg = this.conversationService.chat(singleChatModel).getContent();

        return WxMpXmlOutMessage
                .TEXT()
                .fromUser(wxMessage.getToUser())
                .toUser(username)
                .content(msg)
                .build();
    }
}
