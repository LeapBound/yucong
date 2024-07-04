package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.yc.hub.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.chat.func.MyFunctions;
import com.github.leapbound.yc.hub.entities.FunctionEntity;
import com.github.leapbound.yc.hub.mapper.FunctionMapper;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.ActionServerService;
import com.github.leapbound.yc.hub.service.gpt.FuncService;
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
    private final ObjectMapper mapper;

    @Override
    public List<MyFunctions> getListByAccountIdAndBotId(String accountId, String botId, ProcessTaskDto currentTask) {
        // 获取账号function列表
        List<FunctionEntity> functionList = null;
        if (currentTask == null) {
            functionList = this.functionMapper.listByBotId(botId);
        } else if (StringUtils.hasText(currentTask.getTaskName())) {
            LambdaQueryWrapper<FunctionEntity> lqw = new LambdaQueryWrapper<FunctionEntity>()
                    .eq(FunctionEntity::getFunctionUuid, this.actionServerService.getTaskFunction(currentTask));
            functionList = this.functionMapper.selectList(lqw);
        }

        if (functionList == null || functionList.isEmpty()) {
            return null;
        }

        List<MyFunctions> functions = new ArrayList<>(functionList.size());
        functionList.forEach(entity -> {
            try {
                MyFunctions myFunctions = this.mapper.readValue(entity.getFunctionJson(), MyFunctions.class);
                myFunctions = fillFunctionEnum(myFunctions, currentTask);
                log.info("可以执行的function: {}", myFunctions);
                functions.add(myFunctions);
            } catch (JsonProcessingException e) {
                log.error("getListByAccountIdAndBotId error", e);
            }
        });

        return functions;
    }

    private MyFunctions fillFunctionEnum(MyFunctions myFunctions, ProcessTaskDto currentTask) {
        if (currentTask == null) {
            return myFunctions;
        }

        // 获取需要填充的enum
        Set<String> needFillEnum = new HashSet<>();
        currentTask.getCurrentInputForm().forEach(inputForm -> {
            if (inputForm.getType().equals("enum")) {
                needFillEnum.add(inputForm.getId());
            }
        });

        // 填充enum
        if (!needFillEnum.isEmpty()) {
            myFunctions.getParameters().getProperties().forEach((k, v) -> {
                if (needFillEnum.contains(k)) {
                    Map<String, Object> property = (Map<String, Object>) v;
                    property.put("enum", this.actionServerService.loadTaskFunctionOptions(currentTask));
                }
            });
        }

        return myFunctions;
    }

    @Override
    public FunctionExecResultDto invokeFunc(String botId, String accountId, MyFunctionCall functionCall) {
        return this.actionServerService.invokeFunc(botId, accountId, functionCall);
    }
}
