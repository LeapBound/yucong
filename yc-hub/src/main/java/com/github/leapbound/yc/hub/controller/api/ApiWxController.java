package com.github.leapbound.yc.hub.controller.api;

import com.github.leapbound.yc.hub.vendor.wx.cp.YcWxCpService;
import com.github.leapbound.yc.hub.vendor.wx.mp.YcWxMpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.leapbound.yc.hub.model.R;
import com.github.leapbound.yc.hub.service.ChannelService;

@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/api/wx")
@RequiredArgsConstructor
public class ApiWxController {

    private final YcWxCpService ycWxCpService;
    private final YcWxMpService ycWxMpService;

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

        log.debug("goMp inMessage：\n{} ", inMessage);
        WxMpXmlOutMessage outMessage = this.ycWxMpService.getMpRouter(appId).route(inMessage);

        log.debug("goMp outMessage：\n{}", outMessage);
        return outMessage;
    }

    private WxCpXmlOutMessage goCp(String corpId, Integer agentId, String username, String content) {
        WxCpXmlMessage inMessage = new WxCpXmlMessage();
        inMessage.setMsgType(WxConsts.XmlMsgType.TEXT);
        inMessage.setFromUserName(username);
        inMessage.setContent(content);
        inMessage.setAgentId(agentId.toString());

        log.debug("goCp inMessage：\n{} ", inMessage);
        WxCpXmlOutMessage outMessage = this.ycWxCpService.getCpRouter(corpId, agentId).route(inMessage);

        log.debug("goCp outMessage：\n{}", outMessage);
        return outMessage;
    }
}
