package com.github.leapbound.yc.action.service;

import com.unfbx.chatgpt.entity.chat.Message;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRequest;

/**
 * @author yamath
 * @since 2023/7/12 9:52
 */
public interface YcFunctionOpenaiService {

    Message executeFunctionForOpenai(FunctionExecuteRequest request);

    Message executeGroovyForOpenai(FunctionExecuteRequest request);
}
