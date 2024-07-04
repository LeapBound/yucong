package com.github.leapbound.yc.action.controller;

import cn.hutool.http.Header;
import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRequest;
import com.github.leapbound.yc.action.service.YcFunctionOpenaiService;
import com.unfbx.chatgpt.entity.chat.Message;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.LinkedHashMap;

/**
 * @author yamath
 * @date 2023/7/12 9:48
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
    public ResponseVo<?> executeGroovy(@RequestBody FunctionExecuteRequest request, HttpServletRequest httpServletRequest) {
        setRequest(request, httpServletRequest);
        return this.ycFunctionOpenaiService.executeGroovy(request);
    }

    @PostMapping("/message")
    public Message executeFunction(@RequestBody FunctionExecuteRequest request, HttpServletRequest httpServletRequest) {
        setRequest(request, httpServletRequest);
//        return this.ycFunctionOpenaiService.executeFunctionForOpenai(request);
        return this.ycFunctionOpenaiService.executeGroovyForOpenai(request);
    }

    @PostMapping("/engineMap/reset")
    public void resetEngineMap(@RequestParam(value = "key", required = false) String key) {
        this.ycFunctionOpenaiService.resetEngineMap(key);
    }

    private void setRequest(FunctionExecuteRequest request, HttpServletRequest httpServletRequest) {
        JSONObject header = enumHeaders(httpServletRequest);
        JSONObject args = JSONObject.parseObject(request.getArguments());
        if (args == null) {
            args = header;
        } else {
            args.putAll(header);
        }
        request.setArguments(args.toJSONString());
    }

    private static JSONObject enumHeaders(HttpServletRequest httpServletRequest) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> enumHeaders = httpServletRequest.getHeaderNames();
        final LinkedHashMap<String, Header> map = new LinkedHashMap<>();
        for (final Header e : Header.class.getEnumConstants()) {
            map.put(e.getValue().toLowerCase(), e);
        }
        while (enumHeaders.hasMoreElements()) {
            String headerName = enumHeaders.nextElement();
            if (map.containsKey(headerName.toLowerCase())) {
                continue;
            }
            String headerValue = httpServletRequest.getHeader(headerName);
            jsonObject.put(headerName.toLowerCase(), headerValue);
        }
        return jsonObject;
    }
}
