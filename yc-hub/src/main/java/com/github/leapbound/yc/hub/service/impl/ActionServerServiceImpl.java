package com.github.leapbound.yc.hub.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.model.process.ProcessVariablesRequestDto;
import com.github.leapbound.yc.hub.service.ActionServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Fred
 * @date 2024/4/8 11:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActionServerServiceImpl implements ActionServerService {

    private final RestTemplate actionRestTemplate;

    @Override
    public ProcessTaskDto queryNextTask(String accountId) {
        // 查询是否存在进行中的流程
        ProcessTaskDto task = null;
        try {
            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("accountId", accountId);
            HttpEntity<ProcessVariablesRequestDto> requestEntity = new HttpEntity<>(new ProcessVariablesRequestDto(), requestHeaders);
            ResponseEntity<ProcessTaskDto> entity = this.actionRestTemplate.postForEntity("/business/task", requestEntity, ProcessTaskDto.class);

            task = entity.getBody();
            if (task != null && task.getTaskId() == null) {
                task = null;
            }
        } catch (Exception e) {
            log.error("getTask error", e);
        }

        return task;
    }

    @Override
    public JSONObject loadProcessConfig(String processInstanceId) {
        // 查询是否存在进行中的流程
        JSONObject task = null;
        try {
            // 请求action server执行方法
            ProcessVariablesRequestDto processVariablesRequestDto = new ProcessVariablesRequestDto();
            processVariablesRequestDto.setProcessInstanceId(processInstanceId);
            HttpEntity<String> requestEntity = new HttpEntity<>(processInstanceId);
            ResponseEntity<JSONObject> entity = this.actionRestTemplate.postForEntity("/business/process/variables", requestEntity, JSONObject.class);

            task = entity.getBody();
        } catch (Exception e) {
            log.error("getTask error", e);
        }

        return task;
    }

    @Override
    public MyMessage invokeFunc(String botId, String accountId, MyFunctionCall functionCall) {
        MyMessage message = null;
        try {
            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("accountId", accountId);
            requestHeaders.add("deviceId", "deviceId001");
            // body
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(functionCall);
            log.debug("invokeFunc json: {}", json);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);
            ResponseEntity<MyMessage> entity = this.actionRestTemplate.postForEntity("/function/openai/execute", requestEntity, MyMessage.class);

            message = entity.getBody();
            if (message != null) {
                log.info("执行方法返回: {}", message);
            }
        } catch (Exception e) {
            log.error("invokeFunc error", e);
        }

        return message;
    }
}
