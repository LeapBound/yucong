package yzggy.yucong.action.service;

import com.baomidou.mybatisplus.extension.service.IService;
import yzggy.yucong.action.entities.YcFunctionExecuteRecord;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRecordSaveRequest;

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
