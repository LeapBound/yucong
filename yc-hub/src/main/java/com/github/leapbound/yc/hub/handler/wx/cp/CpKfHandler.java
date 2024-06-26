package com.github.leapbound.yc.hub.handler.wx.cp;

import com.github.leapbound.yc.hub.chat.dialog.MyMessageType;
import com.github.leapbound.yc.hub.consts.RedisConsts;
import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.service.BotService;
import com.github.leapbound.yc.hub.service.ConversationService;
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
    private final BotService botService;
    @Value("${yucong.conversation.expire:300}")
    private int expires;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> map, WxCpService wxCpService, WxSessionManager wxSessionManager) throws WxErrorException {
        log.debug("CpKfHandler 接收到请求消息，WxCpXmlMessage：{} map: {}", wxMessage, map);

        String openKfId = wxMessage.getOpenKfId();
        String botId = this.botService.getBotId(wxCpService.getWxCpConfigStorage().getCorpId(), String.valueOf(wxCpService.getWxCpConfigStorage().getAgentId()));
        String nextCursor = getNextCursor(botId, openKfId);

        WxCpKfServiceImpl wxCpKfService = new WxCpKfServiceImpl(wxCpService);
        WxCpKfMsgListResp wxCpKfMsgListResp = wxCpKfService.syncMsg(nextCursor, wxMessage.getToken(), 100, 0, openKfId);
        log.info("CpKfHandler 接收到请求消息 botId: {}, username: {}, nextCursor: {}", botId, openKfId, wxCpKfMsgListResp.getNextCursor());

        if (wxCpKfMsgListResp != null) {
            nextCursor = wxCpKfMsgListResp.getNextCursor();
            setNextCursor(botId, openKfId, nextCursor);

            AtomicReference<String> externalUserId = new AtomicReference<>();
            StringBuilder sb = new StringBuilder();
            wxCpKfMsgListResp.getMsgList().forEach(wxCpKfMsgItem -> {
                sb.append(wxCpKfMsgItem.getText().getContent()).append("\n");
                externalUserId.set(wxCpKfMsgItem.getExternalUserId());
            });
            log.debug("wxCpKfMsgListResp content {}", sb);

            SingleChatDto singleChatModel = new SingleChatDto();
            singleChatModel.setBotId(botId);
            singleChatModel.setAccountId(openKfId);
            singleChatModel.setContent(sb.toString());
            singleChatModel.setType(MyMessageType.TEXT);
            String msg = this.conversationService.chat(singleChatModel).getContent();
            log.debug("wxCpKfMsgListResp chat {}", msg);

            WxCpKfMsgSendRequest request = new WxCpKfMsgSendRequest();
            request.setToUser(externalUserId.get());
            request.setOpenKfid(openKfId);
            request.setMsgType("text");
            WxCpKfTextMsg wxCpKfTextMsg = new WxCpKfTextMsg();
            wxCpKfTextMsg.setContent(msg);
            request.setText(wxCpKfTextMsg);
            wxCpKfService.sendMsg(request);
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
