package com.github.leapbound.yc.hub.controller.tim;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fred Gu
 * @date 2024-12-03 15:54
 */
@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/tim/callback")
@RequiredArgsConstructor
public class TimCallbackController {

    @RequestMapping
    public Map<String, Object> callback(@RequestBody Map<String, Object> param) {
        log.info("callback param: {}", param);
        Map<String, Object> result = new HashMap<>();
        result.put("ActionStatus", 0);
        result.put("ErrorInfo", "");
        result.put("ErrorCode", 0);
        return result;
    }
}
