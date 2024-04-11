package com.github.leapbound.yc.hub.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.chat.func.MyFunctions;
import com.github.leapbound.yc.hub.entities.FunctionEntity;
import com.github.leapbound.yc.hub.mapper.FunctionMapper;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.ActionServerService;
import com.github.leapbound.yc.hub.service.gpt.FuncService;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncServiceImpl implements FuncService {

    private final ActionServerService actionServerService;
    private final FunctionMapper functionMapper;

    @Override
    public List<MyFunctions> getListByAccountIdAndBotId(String accountId, String botId, ProcessTaskDto task) {
        // 获取账号function列表
        List<FunctionEntity> functionList = null;
        if (task == null) {
            functionList = this.functionMapper.listByBotId(botId);
        } else if (StringUtils.hasText(task.getTaskName())) {
            functionList = this.functionMapper.listByTaskName(task.getTaskName());
        }

        if (functionList != null && !functionList.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            List<MyFunctions> functions = new ArrayList<>(functionList.size());
            if (task != null) {
                JSONObject config = this.actionServerService.loadProcessVariables(task.getProcessInstanceId());
                functionList.forEach(entity -> {
                    try {
                        MyFunctions myFunctions = mapper.readValue(entity.getFunctionJson(), MyFunctions.class);
                        // 填充枚举
                        myFunctions.getParameters().getProperties().forEach((k, v) -> {
                            switch (k) {
                                case "loanTerm":
                                    Map<String, Object> property = (Map<String, Object>) v;
                                    Set<String> termSet = new HashSet<>();
                                    for (Object stage : config.getJSONArray("StageCount")) {
                                        Map<String, Object> stageObject = (Map<String, Object>) stage;
                                        String value = stageObject.get("value").toString();
                                        if (!"请选择".equals(value)) {
                                            termSet.add(value);
                                        }
                                    }
                                    property.put("enum", termSet);
                                    break;
                            }

                        });
                        log.info("可以执行的function: {}", myFunctions);
                        functions.add(myFunctions);
                    } catch (JsonProcessingException e) {
                        log.error("getListByAccountIdAndBotId error", e);
                    }
                });
            } else {
                functionList.forEach(entity -> {
                    try {
                        MyFunctions myFunctions = mapper.readValue(entity.getFunctionJson(), MyFunctions.class);
                        log.info("可以执行的function: {}", myFunctions.getName());
                        functions.add(myFunctions);
                    } catch (JsonProcessingException e) {
                        log.error("getListByAccountIdAndBotId error", e);
                    }
                });
            }
            return functions;
        }

        return null;
    }

    @Override
    public Boolean invokeFunc(String botId, String accountId, MyFunctionCall functionCall) {
        return this.actionServerService.invokeFunc(botId, accountId, functionCall);
    }
}
