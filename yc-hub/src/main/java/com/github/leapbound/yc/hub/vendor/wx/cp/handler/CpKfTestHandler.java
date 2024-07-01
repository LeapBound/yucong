package com.github.leapbound.yc.hub.vendor.wx.cp.handler;

import com.github.leapbound.yc.hub.chat.dialog.MyMessageType;
import com.github.leapbound.yc.hub.model.AccountDto;
import com.github.leapbound.yc.hub.model.ChannelDto;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.ChannelService;
import com.github.leapbound.yc.hub.service.ConversationService;
import com.github.leapbound.yc.hub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Fred
 * @date 2024/6/30 22:34
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CpKfTestHandler extends AbstractHandler {

    private final UserService userService;
    private final ChannelService channelService;
    private final ConversationService conversationService;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> map, WxCpService wxCpService, WxSessionManager wxSessionManager) throws WxErrorException {
        log.debug("CpKfTestHandler 接收到请求消息，WxCpXmlMessage：{} map: {}", wxMessage, map);

        String openKfId = wxMessage.getOpenKfId();
        String externalUserId = wxMessage.getExternalUserId();

        ChannelDto channelDto = this.channelService.getChannelByOpenKfId(openKfId);
        AccountDto accountDto = this.userService.getAccountByChannelIdAndExternalId(channelDto.getChannelId(), externalUserId);

        SingleChatDto singleChatModel = new SingleChatDto();
        singleChatModel.setBotId(channelDto.getBotId());
        singleChatModel.setAccountId(accountDto.getAccountId());
        singleChatModel.setContent(wxMessage.getContent());
        singleChatModel.setType(MyMessageType.TEXT);
        String msg = this.conversationService.chat(singleChatModel, true).getContent();
        log.debug("msg: {}", msg);

        return null;
    }
}
