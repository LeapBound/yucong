package com.github.leapbound.yc.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.leapbound.yc.camunda.entities.ProcessFunctionManage;
import com.github.leapbound.yc.camunda.model.bo.ProcessFunctionSaveRequest;
import com.github.leapbound.yc.camunda.model.vo.R;

/**
 * @author yamath
 * @since 2023/11/24 11:28
 */
public interface FunctionManageService extends IService<ProcessFunctionManage> {

    R<?> saveProcessFunctionManage(ProcessFunctionSaveRequest processFunctionSaveRequest);

    R<?> getProcessFunction(String processKey);
}
