package com.github.leapbound.yc.hub.controller.api;

import com.github.leapbound.yc.hub.model.R;
import com.github.leapbound.yc.hub.model.test.TestFlowDto;
import com.github.leapbound.yc.hub.model.test.TestMessageDto;
import com.github.leapbound.yc.hub.vendor.wx.cp.YcWxCpService;
import com.github.leapbound.yc.hub.vendor.wx.mp.YcWxMpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/api/wx")
@RequiredArgsConstructor
public class ApiWxController {

    private final YcWxCpService ycWxCpService;
    private final YcWxMpService ycWxMpService;

    @PostMapping("/message/receive")
    public R<String> dealMessage(@RequestBody TestFlowDto testFlowDto) {
        TestMessageDto testMessageDto = testFlowDto.getMessages().get(0);
        return switch (testFlowDto.getChannel()) {
            case "wxMp" -> R.ok(this.goMp(testFlowDto.getCorpId(), "username", testMessageDto.getContent()).toString());
            case "wxCp" ->
                    R.ok(this.goCp(testFlowDto.getCorpId(), testFlowDto.getAgentId(), "username", testMessageDto.getContent()).toString());
            default -> R.fail();
        };
    }

    private WxMpXmlOutMessage goMp(String appId, String username, String content) {
        WxMpXmlMessage inMessage = new WxMpXmlMessage();
        inMessage.setMsgType(WxConsts.XmlMsgType.TEXT);
        inMessage.setFromUser(username);
        inMessage.setContent(content);

        log.debug("goMp inMessage：\n{} ", inMessage);
        final WxMpService wxMpService = this.ycWxMpService.getMpService(appId);
        if (wxMpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }
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
        final WxCpService wxCpService = this.ycWxCpService.getCpService(corpId, agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应agentId=[%d]的配置，请核实！", agentId));
        }
        WxCpXmlOutMessage outMessage = this.ycWxCpService.getCpRouter(corpId, agentId).route(inMessage);

        log.debug("goCp outMessage：\n{}", outMessage);
        return outMessage;
    }
}
