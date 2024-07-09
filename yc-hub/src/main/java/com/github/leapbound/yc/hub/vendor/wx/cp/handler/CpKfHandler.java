package com.github.leapbound.yc.hub.vendor.wx.cp.handler;

import com.github.leapbound.sdk.llm.chat.dialog.MyMessageType;
import com.github.leapbound.yc.hub.consts.RedisConsts;
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
import me.chanjar.weixin.cp.api.impl.WxCpKfServiceImpl;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgListResp;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgSendRequest;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfTextMsg;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fred
 * @date 2024/6/25 13:46
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CpKfHandler extends AbstractHandler {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final ConversationService conversationService;
    private final UserService userService;
    private final ChannelService channelService;
    @Value("${yucong.conversation.expire:3600}")
    private int expires;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> map, WxCpService wxCpService, WxSessionManager wxSessionManager) throws WxErrorException {
        log.debug("CpKfHandler 接收到请求消息，WxCpXmlMessage：{} map: {}", wxMessage, map);

        String openKfId = wxMessage.getOpenKfId();
        ChannelDto channelDto = this.channelService.getChannelByOpenKfId(openKfId);
        String botId = channelDto.getBotId();

        String nextCursor = getNextCursor(botId, openKfId);
        WxCpKfServiceImpl wxCpKfService = new WxCpKfServiceImpl(wxCpService);
        WxCpKfMsgListResp wxCpKfMsgListResp = wxCpKfService.syncMsg(nextCursor, wxMessage.getToken(), 100, 0, openKfId);
        log.info("CpKfHandler 接收到请求消息 botId: {}, openKfId: {}, nextCursor: {}", botId, openKfId, wxCpKfMsgListResp.getNextCursor());

        if (wxCpKfMsgListResp != null) {
            nextCursor = wxCpKfMsgListResp.getNextCursor();
            setNextCursor(botId, openKfId, nextCursor);

            Map<String, StringBuilder> messageMap = new HashMap<>();
            wxCpKfMsgListResp.getMsgList().forEach(wxCpKfMsgItem -> {
                if (!StringUtils.hasText(wxCpKfMsgItem.getServicerUserId())) {
                    String externalUserId = wxCpKfMsgItem.getExternalUserId();
                    messageMap.putIfAbsent(externalUserId, new StringBuilder());
                    WxCpKfTextMsg textMsg = wxCpKfMsgItem.getText();
                    if (textMsg != null && StringUtils.hasText(textMsg.getContent())) {
                        messageMap.get(externalUserId).append(textMsg.getContent()).append("\n");
                    }
                }
            });
            log.debug("wxCpKfMsgListResp content {}", messageMap);

            for (String externalUserId : messageMap.keySet()) {
                String accountId = this.userService.getAccountByChannelIdAndExternalId(channelDto.getChannelId(), externalUserId).getAccountId();

                SingleChatDto singleChatModel = new SingleChatDto();
                singleChatModel.setBotId(botId);
                singleChatModel.setAccountId(accountId);
                singleChatModel.setContent(messageMap.get(externalUserId).toString());
                singleChatModel.setType(MyMessageType.TEXT);
                Map<String, Object> params = new HashMap<>();
                params.put("openKfId", openKfId);
                params.put("externalUserId", externalUserId);
                singleChatModel.setParam(params);
                String msg = this.conversationService.chat(singleChatModel).getContent();
                log.debug("wxCpKfMsgListResp chat {}", msg);

                if (StringUtils.hasText(msg)) {
                    WxCpKfMsgSendRequest request = new WxCpKfMsgSendRequest();
                    request.setToUser(externalUserId);
                    request.setOpenKfid(openKfId);
                    request.setMsgType("text");
                    WxCpKfTextMsg wxCpKfTextMsg = new WxCpKfTextMsg();
                    wxCpKfTextMsg.setContent(msg);
                    request.setText(wxCpKfTextMsg);
                    wxCpKfService.sendMsg(request);
                }
            }
        }

        return null;
    }

    private String getNextCursor(String botId, String openKfId) {
        return (String) this.redisTemplate.opsForHash().get(RedisConsts.WX_CP_KF_NEXT_CURSOR_MAP_KEY, botId + openKfId);
    }

    private void setNextCursor(String botId, String openKfId, String nextCursor) {
        this.redisTemplate.opsForHash().put(RedisConsts.WX_CP_KF_NEXT_CURSOR_MAP_KEY, botId + openKfId, nextCursor);
    }
}
