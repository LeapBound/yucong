package com.github.leapbound.yc.hub.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.consts.ProcessConsts;
import com.github.leapbound.yc.hub.model.process.ProcessRequestDto;
import com.github.leapbound.yc.hub.model.process.ProcessResponseDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.ActionServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Fred
 * @date 2024/4/8 11:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActionServerServiceImpl implements ActionServerService {

    private final RestTemplate actionRestTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ProcessTaskDto queryNextTask(String accountId) {
        // 查询是否存在进行中的流程
        ProcessTaskDto task = null;
        try {
            // 请求action server执行方法
            ProcessRequestDto processRequestDto = new ProcessRequestDto();
            processRequestDto.setBusinessKey(accountId);
            HttpEntity<ProcessRequestDto> requestEntity = new HttpEntity<>(processRequestDto);
            ResponseEntity<ProcessResponseDto<ProcessTaskDto>> entity = this.actionRestTemplate.exchange(
                    "/business/task", HttpMethod.POST, requestEntity,
                    new ParameterizedTypeReference<>() {
                    });

            task = entity.getBody().getData();
            log.debug("queryNextTask {} {}", task != null ? task.getTaskName() : null, task);
            if (task != null && task.getTaskId() == null) {
                task = null;
            }
        } catch (Exception e) {
            log.error("getTask error", e);
        }

        return task;
    }

    @Override
    public String getProcessTaskRemind(String accountId, ProcessTaskDto currentTask, Boolean functionExecuteResult) {
        if (functionExecuteResult != null) {
            // 用户触发了开始流程前的function，currentTask为空
            if (currentTask == null) {
                return getNextTaskRemind(queryNextTask(accountId));
            }

            // 触发了流程中的function
            if (functionExecuteResult) {
                String afterRemindSuccess = getTaskProperty(currentTask, ProcessConsts.TASK_REMIND_AFTER_SUCCESS);
                // 判断当前task是否有结束提醒
                if (StringUtils.hasText(afterRemindSuccess)) {
                    return afterRemindSuccess;
                } else {
                    return getNextTaskRemind(queryNextTask(accountId));
                }
            } else {
                return getTaskProperty(currentTask, ProcessConsts.TASK_REMIND_AFTER_FAIL);
            }
        } else {
            if (currentTask == null) {
                // 没有执行function，没有需要完成的task
                return null;
            } else {
                // 没有执行function，但是有需要完成的task
                return null;
            }
        }
    }

    private String getNextTaskRemind(ProcessTaskDto nextTask) {
        String beforeRemind = getTaskProperty(nextTask, ProcessConsts.TASK_REMIND_BEFORE);

        Set<String> optionSet = loadTaskFunctionOptions(nextTask);
        if (optionSet != null && !optionSet.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.hasText(beforeRemind)) {
                sb.append(beforeRemind).append(":\n\n");
            }

            int i = 1;
            for (String stage : optionSet) {
                if (!"请选择".equals(stage)) {
                    sb.append(i).append(". ").append(stage).append("\n");
                    i++;
                }
            }

            return sb.toString();
        }

        return beforeRemind;
    }

    @Override
    public JSONObject loadProcessVariables(String processInstanceId) {
        // 查询是否存在进行中的流程
        JSONObject task = null;
        try {
            // 请求action server执行方法
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ProcessRequestDto processRequestDto = new ProcessRequestDto();
            processRequestDto.setProcessInstanceId(processInstanceId);
            HttpEntity<ProcessRequestDto> requestEntity = new HttpEntity<>(processRequestDto, headers);
            ResponseEntity<ProcessResponseDto<JSONObject>> entity = this.actionRestTemplate.exchange(
                    "/business/process/variables", HttpMethod.POST, requestEntity,
                    new ParameterizedTypeReference<>() {
                    });

            task = entity.getBody().getData();
            log.debug("loadProcessVariables {}", task);
        } catch (Exception e) {
            log.error("loadProcessVariables error", e);
        }

        return task;
    }

    @Override
    public void deleteProcess(String processInstanceId) {
        // 请求action server执行方法
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // 创建表单数据
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("processInstanceId", processInstanceId);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
            ResponseEntity<ProcessResponseDto> entity = this.actionRestTemplate.postForEntity(
                    "/business/process/delete", requestEntity, ProcessResponseDto.class
            );

            log.debug("deleteProcess {}", entity.getBody());
        } catch (Exception e) {
            log.error("deleteProcess error", e);
        }
    }

    @Override
    public void inputProcessVariable(String processInstanceId, String businessKey, Map<String, Object> params) {
        // 请求action server执行方法
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // 创建表单数据
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("processInstanceId", processInstanceId);
            formData.add("businessKey", businessKey);
            formData.add("inputVariables", params);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
            ResponseEntity<ProcessResponseDto> entity = this.actionRestTemplate.postForEntity(
                    "/process/variables/input", requestEntity, ProcessResponseDto.class
            );

            log.debug("inputProcessVariable {}", entity.getBody());
        } catch (Exception e) {
            log.error("inputProcessVariable error", e);
        }
    }

    @Override
    public Boolean invokeFunc(String botId, String accountId, MyFunctionCall functionCall) {
        try {
            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("accountId", accountId);
            requestHeaders.add("botId", botId);
            // todo
            requestHeaders.add("deviceId", "deviceId001");
            // body
            String json = this.objectMapper.writeValueAsString(functionCall);
            log.debug("invokeFunc json {}", json);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);
            ResponseEntity<ProcessResponseDto> entity = this.actionRestTemplate.postForEntity(
                    "/function/openai/execute",
                    requestEntity,
                    ProcessResponseDto.class);

            log.debug("invokeFunc {}", entity.getBody());
            return entity.getBody().getSuccess();
        } catch (Exception e) {
            log.error("invokeFunc error", e);
        }

        return false;
    }

    @Override
    public Set<String> loadTaskFunctionOptions(ProcessTaskDto task) {
        Map<String, String> showVariableMap = getTaskProperty(task, ProcessConsts.TASK_SHOW_VARIABLE);
        if (showVariableMap != null) {
            String showVariable = showVariableMap.get("name");
            JSONObject config = loadProcessVariables(task.getProcessInstanceId());

            switch (showVariableMap.get("type")) {
                case "set":
                case "list":
                    List<String> configList = (config.getObject(showVariable, List.class));
                    return configList.stream().collect(Collectors.toSet());
                case "map":
                    Map<String, String> configMap = (config.getObject(showVariable, Map.class));
                    return configMap.keySet().stream().collect(Collectors.toSet());
            }
        }

        return null;
    }

    @Override
    public String getTaskFunction(ProcessTaskDto task) {
        return getTaskProperty(task, ProcessConsts.TASK_FUNCTION_UUID);
    }

    private <T> T getTaskProperty(ProcessTaskDto task, String name) {
        if (task == null) {
            return null;
        }

        AtomicReference<T> type = new AtomicReference<>();
        task.getTaskProperties().stream()
                .filter(property -> {
                    String propertyName = (String) property.get("name");
                    return propertyName.equals(name);
                })
                .findFirst()
                .ifPresent(property -> type.set((T) property.get("type")));
        return type.get();
    }

}
