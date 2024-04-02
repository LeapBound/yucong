package com.github.leapbound.yc.action.controller;

import cn.hutool.core.util.StrUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRequest;
import com.github.leapbound.yc.action.service.YcFunctionOpenaiService;

/**
 * @author yamath
 * @since 2023/7/12 9:48
 */
@RestController
@RequestMapping("/function/openai")
public class YcFunctionOpenaiController {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionOpenaiController.class);

    private final YcFunctionOpenaiService ycFunctionOpenaiService;

    public YcFunctionOpenaiController(YcFunctionOpenaiService ycFunctionOpenaiService) {
        this.ycFunctionOpenaiService = ycFunctionOpenaiService;
    }

    @PostMapping("/execute")
    public Message executeFunction(@RequestBody FunctionExecuteRequest request, HttpServletRequest httpServletRequest) {
        String userName = httpServletRequest.getHeader("userName");
        String accountId = httpServletRequest.getHeader("accountId");
        String deviceId = httpServletRequest.getHeader("deviceId");
        logger.info("function execute request: {}, userName: {}, accountId: {}", request, userName, accountId);

        if (!StrUtil.isEmptyIfStr(userName)) {
            request.setUserName(userName);
        }
        if (!StrUtil.isEmptyIfStr(accountId)) {
            request.setAccountId(accountId);
        }
        if (!StrUtil.isEmptyIfStr(deviceId)) {
            request.setDeviceId(deviceId);
        }
//        return this.ycFunctionOpenaiService.executeFunctionForOpenai(request); // java class
        return this.ycFunctionOpenaiService.executeGroovyForOpenai(request); // groovy script
    }
}
