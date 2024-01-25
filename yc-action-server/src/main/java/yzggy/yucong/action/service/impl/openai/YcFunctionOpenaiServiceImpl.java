package yzggy.yucong.action.service.impl.openai;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unfbx.chatgpt.entity.chat.Message;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import yzggy.yucong.action.config.RedisConfig;
import yzggy.yucong.action.func.FunctionExecutor;
import yzggy.yucong.action.func.FunctionGroovyExec;
import yzggy.yucong.action.model.dto.YcFunctionGroovyDto;
import yzggy.yucong.action.model.dto.YcFunctionMethodDto;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRecordSaveRequest;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRequest;
import yzggy.yucong.action.service.YcFunctionGroovyService;
import yzggy.yucong.action.service.YcFunctionMethodService;
import yzggy.yucong.action.service.YcFunctionOpenaiService;

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

    public YcFunctionOpenaiServiceImpl(
            YcFunctionMethodService ycFunctionMethodService,
            YcFunctionGroovyService ycFunctionGroovyService,
            RedisTemplate redisTemplate) {
        this.ycFunctionMethodService = ycFunctionMethodService;
        this.ycFunctionGroovyService = ycFunctionGroovyService;
        this.redisTemplate = redisTemplate;
    }

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
            if (!StrUtil.isEmptyIfStr(request.getAccountId())) {
                arguments.put("accountId", request.getAccountId());
            }
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
        YcFunctionGroovyDto dto = this.ycFunctionGroovyService.getFunctionGroovyDto(functionName);
        if (dto == null) {
            logger.warn("no data found in [yc_function_groovy], function = {}", functionName);
            return null;
        }
        //
        try {
            // execute
            JSONObject arguments = JSON.parseObject(request.getArguments());
            if (!StrUtil.isEmptyIfStr(request.getAccountId())) {
                arguments.put("accountId", request.getAccountId());
            }
            // execute
            JSONObject jsonObject = null;
            String engineKey = dto.getGroovyName();
            if (engineMap.containsKey(engineKey)) {
                GroovyScriptEngine engine = engineMap.get(engineKey);
                jsonObject = FunctionGroovyExec.runScript(engine, dto, arguments);
            } else {
                GroovyScriptEngine engine = FunctionGroovyExec.createGroovyEngine(dto.getGroovyUrl());
                if (engine != null) {
                    engineMap.put(engineKey, engine);
                    jsonObject = FunctionGroovyExec.runScript(engine, dto, arguments);
                }
            }
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
