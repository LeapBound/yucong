package com.github.leapbound.yc.action.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yamath
 * @since 2023/7/17 10:37
 */
@RestController
@RequestMapping("/mock")
public class MockController {

    @PostMapping("/account/close")
    public JSONObject mockCloseAccount(@RequestParam("account") String account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        jsonObject.put("操作结果", "成功关闭账号");
        return jsonObject;
    }
}
