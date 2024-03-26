package com.github.leapbound.yc.hub.controller.wx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.crypto.WxMpCryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.github.leapbound.yc.hub.service.ChannelService;

@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/wx/mp/portal/{appId}")
@RequiredArgsConstructor
public class MpPortalController {

    private final ChannelService channelService;

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String authGet(@PathVariable String appId,
                          @RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "encrypt_type", required = false) String encryptType,
                          @RequestParam(name = "echostr", required = false) String echostr) {
        log.debug("接收到来自微信服务器MP的认证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]",
                signature, timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        final WxMpService wxMpService = this.channelService.getMpService(appId);
        if (wxMpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }

        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            if (encryptType == null) {
                return echostr;
            } else {
                return new WxMpCryptUtil(wxMpService.getWxMpConfigStorage()).decrypt(echostr);
            }
        }

        return "非法请求";
    }

    @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public String post(@PathVariable String appId,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam(name = "encrypt_type", required = false) String encryptType) {
        log.debug("接收微信请求：[signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, timestamp, nonce, requestBody);

        final WxMpService wxMpService = this.channelService.getMpService(appId);
        if (wxMpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }

        WxMpXmlMessage inMessage;
        if (encryptType == null) {
            inMessage = WxMpXmlMessage.fromXml(requestBody);
        } else {
            inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxMpService.getWxMpConfigStorage(),
                    timestamp, nonce, signature);
        }
        log.debug("消息解密后内容为：\n{} ", inMessage);
        WxMpXmlOutMessage outMessage = this.route(appId, inMessage);
        if (outMessage == null) {
            return "";
        }

        String out;
        if (encryptType == null) {
            out = outMessage.toXml();
        } else {
            out = outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
        }
        log.debug("组装回复信息：{}", out);
        return out;
    }

    private WxMpXmlOutMessage route(String appId, WxMpXmlMessage message) {
        try {
            return this.channelService.getMpRouter(appId).route(message);
        } catch (Exception e) {
            log.error("route", e);
        }

        return null;
    }
}
