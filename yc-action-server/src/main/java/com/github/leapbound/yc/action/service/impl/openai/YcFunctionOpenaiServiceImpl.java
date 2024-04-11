package com.github.leapbound.yc.action.service.impl.openai;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.action.config.RedisConfig;
import com.github.leapbound.yc.action.func.FunctionExecutor;
import com.github.leapbound.yc.action.func.FunctionGroovyExec;
import com.github.leapbound.yc.action.model.dto.YcFunctionGroovyDto;
import com.github.leapbound.yc.action.model.dto.YcFunctionMethodDto;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRecordSaveRequest;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRequest;
import com.github.leapbound.yc.action.service.YcFunctionGroovyService;
import com.github.leapbound.yc.action.service.YcFunctionMethodService;
import com.github.leapbound.yc.action.service.YcFunctionOpenaiService;
import com.unfbx.chatgpt.entity.chat.Message;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yamath
 * @since 2023/7/12 9:53
 */
@Service
public class YcFunctionOpenaiServiceImpl implements YcFunctionOpenaiService {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionOpenaiServiceImpl.class);

    private final YcFunctionMethodService ycFunctionMethodService;
    private final YcFunctionGroovyService ycFunctionGroovyService;
    private final RedisTemplate redisTemplate;
    private final ConcurrentHashMap<String, GroovyScriptEngine> engineMap = new ConcurrentHashMap<>();

    @Value("${yc.action.external.host:}")
    private String externalHost;

    public YcFunctionOpenaiServiceImpl(
            YcFunctionMethodService ycFunctionMethodService,
            YcFunctionGroovyService ycFunctionGroovyService,
            RedisTemplate redisTemplate) {
        this.ycFunctionMethodService = ycFunctionMethodService;
        this.ycFunctionGroovyService = ycFunctionGroovyService;
        this.redisTemplate = redisTemplate;
    }

    @Deprecated
    @Override
    public Message executeFunctionForOpenai(FunctionExecuteRequest request) {
        // if no parameter
        if (StrUtil.isEmptyIfStr(request.getName())) {
            return null;
        }
        //
        LocalDateTime startTime = LocalDateTime.now();
        String executeResult = "";
        //
        String functionName = request.getName();
        // select function method
        YcFunctionMethodDto dto = this.ycFunctionMethodService.getFunctionMethodDto(functionName);
        if (dto == null) {
            logger.warn("no data found in [yc_function_method], function = {}", functionName);
            return null;
        }
        //
        try {
            JSONObject arguments = JSON.parseObject(request.getArguments());
            arguments.put("functionName", request.getName());
            // execute
            JSONObject jsonObject = FunctionExecutor.execute(dto, arguments);
            // return result
            if (jsonObject != null) {
                executeResult = JSON.toJSONString(jsonObject);
                //
                return Message.builder().name(functionName)
                        .role(Message.Role.FUNCTION)
                        .content(executeResult)
                        .build();
            }
            // jsonObject == null
            executeResult = "执行方法没有结果返回，联系管理员。";
            // no result
            return Message.builder().name(functionName)
                    .role(Message.Role.FUNCTION)
                    .content(executeResult)
                    .build();
        } catch (Exception ex) {
            logger.error("execute {} error, arguments = {}", request.getName(), request.getArguments(), ex);
            executeResult = "执行方法失败，联系管理员。";
            return Message.builder().name(functionName)
                    .role(Message.Role.FUNCTION)
                    .content(executeResult)
                    .build();
        } finally {
            this.saveFunctionExecuteRecord(request, startTime, executeResult);
        }
    }

    @Override
    public Message executeGroovyForOpenai(FunctionExecuteRequest request) {
        //
        LocalDateTime startTime = LocalDateTime.now();
        String executeResult = "";
        String functionName = request.getName();
        //
        try {
            JSONObject jsonObject = executeGroovy(request);
            // return result
            if (jsonObject != null) {
                if (jsonObject.containsKey("functionContent")) {
                    executeResult = jsonObject.getString("functionContent");
                } else {
                    executeResult = JSON.toJSONString(jsonObject);
                }
                //
                return Message.builder().name(functionName)
                        .role(Message.Role.FUNCTION)
                        .content(executeResult)
                        .build();
            }
            // jsonObject == null
            executeResult = "执行方法没有结果返回，联系管理员。";
            // no result
            return Message.builder().name(functionName)
                    .role(Message.Role.FUNCTION)
                    .content(executeResult)
                    .build();
        } catch (Exception ex) {
            logger.error("execute {} error, arguments = {}", request.getName(), request.getArguments(), ex);
            executeResult = "执行方法失败，联系管理员。";
            return Message.builder().name(functionName)
                    .role(Message.Role.FUNCTION)
                    .content(executeResult)
                    .build();
        } finally {
            this.saveFunctionExecuteRecord(request, startTime, executeResult);
        }
    }

    @Override
    public JSONObject executeGroovy(FunctionExecuteRequest request) {
        // if no function
        if (StrUtil.isEmptyIfStr(request.getName())) {
            return null;
        }
        //
        String functionName = request.getName();
        // select function method
        YcFunctionGroovyDto dto = this.ycFunctionGroovyService.getFunctionGroovyDto(functionName);
        if (dto == null) {
            logger.warn("no data found in [yc_function_groovy], function = {}", functionName);
            return null;
        }
        //
        try {
            // execute
            JSONObject arguments = new JSONObject();
            if (!StrUtil.isEmptyIfStr(request.getArguments())) {
                arguments = JSON.parseObject(request.getArguments());
            }
            // groovy external host
            if (!StrUtil.isEmptyIfStr(externalHost)) {
                arguments.put("externalHost", externalHost);
            }
            // execute
            JSONObject result = null;
            String engineKey = dto.getGroovyName();
            if (engineMap.containsKey(engineKey)) {
                GroovyScriptEngine engine = engineMap.get(engineKey);
                result = FunctionGroovyExec.runScript(engine, dto, arguments);
            } else {
                GroovyScriptEngine engine = FunctionGroovyExec.createGroovyEngine(dto.getGroovyUrl());
                if (engine != null) {
                    engineMap.put(engineKey, engine);
                    result = FunctionGroovyExec.runScript(engine, dto, arguments);
                }
            }
            return result;
        } catch (Exception ex) {
            logger.error("execute groovy function error, function = {}, arguments = {}", functionName, request.getArguments(), ex);
        }
        return null;
    }

    @Override
    public JSONObject executeGroovy(String name, String arguments) throws Exception {
        YcFunctionGroovyDto dto = this.ycFunctionGroovyService.getFunctionGroovyDto(name);
        if (dto == null) {
            logger.warn("no data found in [yc_function_groovy], function = {}", name);
            throw new Exception("no data found in [yc_function_groovy], function = " + name);
        }
        JSONObject args = JSON.parseObject(arguments);
        if (!StrUtil.isEmptyIfStr(externalHost)) {
            args.put("externalHost", externalHost);
        }
        String engineKey = dto.getGroovyName();
        if (engineMap.containsKey(engineKey)) {
            GroovyScriptEngine engine = engineMap.get(engineKey);
            return FunctionGroovyExec.runScript(engine, dto, args);
        }
        GroovyScriptEngine engine = FunctionGroovyExec.createGroovyEngine(dto.getGroovyUrl());
        if (engine != null) {
            engineMap.put(engineKey, engine);
            return FunctionGroovyExec.runScript(engine, dto, args);
        }
        return null;
    }

    @Override
    public void resetEngineMap(String key) {
        if (StrUtil.isEmptyIfStr(key)) {
            engineMap.clear();
            return;
        }
        engineMap.remove(key);
    }

    private void saveFunctionExecuteRecord(FunctionExecuteRequest request, LocalDateTime startTime, String result) {
        try {
            LocalDateTime endTime = LocalDateTime.now();
            // record save request
            FunctionExecuteRecordSaveRequest recordSaveRequest = new FunctionExecuteRecordSaveRequest();
            recordSaveRequest.setFunctionName(request.getName());
            recordSaveRequest.setExecuteArguments(request.getArguments());
            recordSaveRequest.setExecuteUser(request.getUserName());
            recordSaveRequest.setExecuteTime(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            recordSaveRequest.setResultTime(endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            recordSaveRequest.setExecuteDuration(Duration.between(startTime, endTime).toMillis());
            recordSaveRequest.setExecuteResult(result);
            // save record
            redisTemplate.convertAndSend(RedisConfig.REDIS_CHANNEL_TOPIC_FUNCTION_CALL_RECORD, recordSaveRequest);
        } catch (Exception ex) {
            logger.error("redis publish message to save function record error", ex);
        }
    }
}
