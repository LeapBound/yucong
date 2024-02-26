package com.github.leapbound.yc.hub.controller.api;

import cn.xfyun.util.CryptTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/asr")
@RequiredArgsConstructor
public class ApiAsrController {

    @PostMapping("/signature/generate")
    public String generateWebsocketUrl(@RequestBody Map<String, Object> reqMap) throws SignatureException {
        String appId = "62770ff4";
        String apiKey = "24fe9331578fa00cd3fef7ba3b5f28a8";
        return CryptTools.hmacEncrypt(CryptTools.HMAC_SHA1, CryptTools.md5Encrypt(appId + reqMap.get("ts")), apiKey);
    }
}
