package com.github.leapbound.yc.action.service;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRequest;
import com.unfbx.chatgpt.entity.chat.Message;

/**
 * @author yamath
 * @since 2023/7/12 9:52
 */
public interface YcFunctionOpenaiService {

    Message executeFunctionForOpenai(FunctionExecuteRequest request);

    Message executeGroovyForOpenai(FunctionExecuteRequest request);

    ResponseVo<?> executeGroovy(FunctionExecuteRequest request);

    JSONObject executeGroovy(String name, String arguments) throws Exception;

    void resetEngineMap(String key);
}
