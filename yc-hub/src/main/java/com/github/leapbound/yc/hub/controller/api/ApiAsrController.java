package com.github.leapbound.yc.hub.controller.api;

import cn.xfyun.util.CryptTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/api/asr")
@RequiredArgsConstructor
public class ApiAsrController {

    @Value("${xfyun.asr.appId}")
    private String appId;
    @Value("${xfyun.asr.apiKey}")
    private String apiKey;

    @PostMapping("/signature/generate")
    public String generateWebsocketUrl(@RequestBody Map<String, Object> reqMap) throws SignatureException {
        return CryptTools.hmacEncrypt(CryptTools.HMAC_SHA1, CryptTools.md5Encrypt(this.appId + reqMap.get("ts")), this.apiKey);
    }
}
