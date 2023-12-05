package yzggy.yucong.handler.wx.mp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.BotService;
import yzggy.yucong.service.ConversationService;

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

        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(this.botService.getBotId(wxMpService.getWxMpConfigStorage().getAppId()));
        singleChatModel.setAccountId(username);
        singleChatModel.setContent(content);
        String msg = this.conversationService.chat(singleChatModel);

        return WxMpXmlOutMessage
                .TEXT()
                .toUser(username)
                .content(msg)
                .build();
    }
}
