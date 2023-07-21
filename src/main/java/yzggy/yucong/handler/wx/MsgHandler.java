package yzggy.yucong.handler.wx;

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
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.GptService;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MsgHandler extends AbstractHandler {

    private final GptService gptService;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> map, WxCpService wxCpService, WxSessionManager wxSessionManager) throws WxErrorException {
        log.debug("MsgHandler 接收到请求消息，WxCpXmlMessage：{} map: {}", wxMessage, map);

        String username = wxMessage.getFromUserName();
        String content = wxMessage.getContent();
        log.info("MsgHandler 接收到请求消息 username: {} content: {}", username, content);

        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(wxMessage.getAgentId());
        singleChatModel.setAccountId(username);
        singleChatModel.setContent(content);
        String msg = this.gptService.chat(singleChatModel);

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
            log.error("send error", e);
        }

        return null;
    }
}