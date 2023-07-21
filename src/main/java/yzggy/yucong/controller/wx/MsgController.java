package yzggy.yucong.controller.wx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutTextMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.config.WxCpConfiguration;
import yzggy.yucong.model.R;

@Slf4j
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MsgController {

    @PostMapping("/receive")
    public R<String> dealMessage(@RequestParam String corpId,
                                 @RequestParam Integer agentId,
                                 @RequestParam String username,
                                 @RequestParam String content) {
        WxCpXmlMessage inMessage = new WxCpXmlMessage();
        inMessage.setContent(content);
        inMessage.setFromUserName(username);
        inMessage.setAgentId(agentId.toString());

        log.debug("消息解密后内容为：\n{} ", inMessage);
        WxCpXmlOutTextMessage outMessage = (WxCpXmlOutTextMessage) this.route(corpId, agentId, inMessage);
        if (outMessage == null) {
            return R.fail();
        }

        log.debug("组装回复信息：{}", outMessage);
        return R.ok(outMessage.getContent());
    }

    private WxCpXmlOutMessage route(String corpId, Integer agentId, WxCpXmlMessage message) {
        try {
            return WxCpConfiguration.getRouters().get(corpId + agentId).route(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

}
