package com.github.leapbound.yc.action.service;

import com.github.leapbound.yc.action.entities.YcFunctionTask;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionTaskRequest;

/**
 *
 *
 * @author tangxu
 * @since 2024/3/29 15:16
 */
public interface YcFunctionTaskService {
    YcFunctionTask queryFunctionTask(String processId, String functionName);
    ResponseVo<Void> saveFunctionTask(FunctionTaskRequest request);

    ResponseVo<Void> updateFunctionTask(FunctionTaskRequest request);

    ResponseVo<Void> deleteFunctionTask(String processId, String functionName, String taskName, String userName);

}
