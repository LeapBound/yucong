package com.github.leapbound.yc.action.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.leapbound.yc.action.entities.YcFunctionExecuteRecord;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRecordSaveRequest;

/**
 * @author yamath
 * @since 2023/7/14 9:38
 */
public interface YcFunctionExecuteRecordService extends IService<YcFunctionExecuteRecord> {

    /**
     * save function call record
     *
     * @param request save request
     */
    void saveFunctionExecuteRecord(FunctionExecuteRecordSaveRequest request);
}
