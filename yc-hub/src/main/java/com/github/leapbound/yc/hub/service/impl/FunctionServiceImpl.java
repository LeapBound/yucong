package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.sdk.llm.chat.func.MyFunctions;
import com.github.leapbound.sdk.llm.chat.func.MyParameters;
import com.github.leapbound.yc.hub.entities.FunctionEntity;
import com.github.leapbound.yc.hub.mapper.FunctionMapper;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.FuncService;
import com.github.leapbound.yc.hub.service.YcActionServerService;
import com.github.leapbound.yc.hub.service.YcProcessService;
import com.github.leapbound.yc.hub.service.impl.function.common.ProcessFunction;
import com.github.leapbound.yc.hub.utils.bean.FunctionBeanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Fred Gu
 * @date 2024-12-04 11:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FuncService {

    private final YcActionServerService actionServerService;
    private final YcProcessService ycProcessService;
    private final FunctionMapper functionMapper;
    private final ProcessFunction processFunction;

    @Override
    public List<MyFunctions> getListByAccountIdAndBotId(String accountId, String botId, ProcessTaskDto currentTask) {
        // 获取账号function列表
        List<FunctionEntity> functionList = null;
        if (currentTask == null) {
            functionList = this.functionMapper.listByBotId(botId);
        } else if (StringUtils.hasText(currentTask.getTaskName())) {
            LambdaQueryWrapper<FunctionEntity> lqw = new LambdaQueryWrapper<FunctionEntity>()
                    .eq(FunctionEntity::getFunctionUuid, this.ycProcessService.getTaskFunction(currentTask));
            functionList = this.functionMapper.selectList(lqw);
        }

        if (functionList == null || functionList.isEmpty()) {
            return null;
        }

        return functionList.stream()
                .map(f -> {
                    MyFunctions myFunctions = FunctionBeanMapper.mapFunctionEntityToMyFunction(f);
                    return fillFunctionEnum(myFunctions, currentTask);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
                    property.put("enum", this.ycProcessService.loadTaskFunctionOptions(currentTask, false));
                }
            });
        }

        return myFunctions;
    }

    @Override
    public FunctionExecResultDto invokeFunc(String botId, String accountId, MyFunctionCall functionCall) {
        // 判断是内置function，还是扩展的
        LambdaQueryWrapper<FunctionEntity> lqw = new LambdaQueryWrapper<FunctionEntity>()
                .eq(FunctionEntity::getFunctionName, functionCall.getName());
        FunctionEntity functionEntity = this.functionMapper.selectOne(lqw);

        if (functionEntity.isExtend()) {
            return this.actionServerService.invokeFunc(botId, accountId, functionCall);
        } else {
            switch (functionCall.getName()) {
                case "start_loan_process":
                case "start_ticket":
                default:
                    return this.processFunction.exec(botId, accountId, functionCall);
            }
        }
    }
}
