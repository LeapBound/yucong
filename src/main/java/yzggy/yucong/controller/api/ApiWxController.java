package yzggy.yucong.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.model.R;
import yzggy.yucong.service.ChannelService;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/wx")
@RequiredArgsConstructor
public class ApiWxController {

    private final ChannelService channelService;

    @PostMapping("/message/receive")
    public R<String> dealMessage(@RequestParam String type,
                                 @RequestParam String corpId,
                                 @RequestParam(required = false) Integer agentId,
                                 @RequestParam String username,
                                 @RequestParam String content) {
        return switch (type) {
            case "mp" -> R.ok(this.goMp(corpId, username, content).toString());
            case "cp" -> R.ok(this.goCp(corpId, agentId, username, content).toString());
            default -> R.fail();
        };
    }

    private WxMpXmlOutMessage goMp(String appId, String username, String content) {
        WxMpXmlMessage inMessage = new WxMpXmlMessage();
        inMessage.setMsgType(WxConsts.XmlMsgType.TEXT);
        inMessage.setFromUser(username);
        inMessage.setContent(content);

        log.debug("消息解密后内容为：\n{} ", inMessage);
        WxMpService wxMpService = this.channelService.getMpService(appId);
        WxMpXmlOutMessage outMessage = this.channelService.getMpRouter(appId).route(inMessage, new HashMap<>(), wxMpService);

        log.debug("组装回复信息：\n{}", outMessage);
        return outMessage;
    }

    private WxCpXmlOutMessage goCp(String corpId, Integer agentId, String username, String content) {
        WxCpXmlMessage inMessage = new WxCpXmlMessage();
        inMessage.setMsgType(WxConsts.XmlMsgType.TEXT);
        inMessage.setFromUserName(username);
        inMessage.setContent(content);
        inMessage.setAgentId(agentId.toString());

        log.debug("消息解密后内容为：\n{} ", inMessage);
        WxCpXmlOutMessage outMessage = this.channelService.getCpRouter(corpId, agentId).route(inMessage);

        log.debug("组装回复信息：\n{}", outMessage);
        return outMessage;
    }
}
