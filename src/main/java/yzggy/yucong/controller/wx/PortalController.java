package yzggy.yucong.controller.wx;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import yzggy.yucong.config.WxCpConfiguration;

@Slf4j
@RestController
@RequestMapping("/wx/cp/portal/{corpId}/{agentId}")
public class PortalController {

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String corpId,
                          @PathVariable Integer agentId,
                          @RequestParam(name = "msg_signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {
        log.debug("接收到来自微信服务器的认证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]",
                signature, timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        final WxCpService wxCpService = WxCpConfiguration.getCpService(corpId, agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应agentId=[%d]的配置，请核实！", agentId));
        }

        if (wxCpService.checkSignature(signature, timestamp, nonce, echostr)) {
            return new WxCpCryptUtil(wxCpService.getWxCpConfigStorage()).decrypt(echostr);
        }

        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String corpId,
                       @PathVariable Integer agentId,
                       @RequestBody String requestBody,
                       @RequestParam("msg_signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce) {
        log.debug("接收微信请求：[signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, timestamp, nonce, requestBody);

        final WxCpService wxCpService = WxCpConfiguration.getCpService(corpId, agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应agentId=[%d]的配置，请核实！", agentId));
        }

        WxCpXmlMessage inMessage = WxCpXmlMessage.fromEncryptedXml(requestBody, wxCpService.getWxCpConfigStorage(),
                timestamp, nonce, signature);
        log.debug("消息解密后内容为：\n{} ", inMessage);
        WxCpXmlOutMessage outMessage = this.route(corpId, agentId, inMessage);
        if (outMessage == null) {
            return "";
        }

        String out = outMessage.toEncryptedXml(wxCpService.getWxCpConfigStorage());
        log.debug("组装回复信息：{}", out);
        return out;
    }

    private WxCpXmlOutMessage route(String corpId, Integer agentId, WxCpXmlMessage message) {
        try {
            return WxCpConfiguration.getRouters().get(corpId + agentId).route(message);
        } catch (Exception e) {
            log.error("route", e);
        }

        return null;
    }
}
