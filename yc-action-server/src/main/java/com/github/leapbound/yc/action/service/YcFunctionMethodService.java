package com.github.leapbound.yc.action.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.leapbound.yc.action.entities.YcFunctionMethod;
import com.github.leapbound.yc.action.model.dto.YcFunctionMethodDto;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionMethodSaveRequest;

/**
 * @author yamath
 * @since 2023/7/11 10:14
 */
public interface YcFunctionMethodService extends IService<YcFunctionMethod> {

    ResponseVo<Void> saveFunctionMethod(FunctionMethodSaveRequest request);

    ResponseVo<Void> updateFunctionMethod(FunctionMethodSaveRequest request);

    ResponseVo<Void> deleteFunctionMethod(String functionName,
                                          String userName);

    YcFunctionMethodDto getFunctionMethodDto(String functionName);
}
