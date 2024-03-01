package com.github.leapbound.yc.action.service.impl.manage;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.github.leapbound.yc.action.entities.YcFunctionExecuteRecord;
import com.github.leapbound.yc.action.mapper.YcFunctionExecuteRecordMapper;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRecordSaveRequest;
import com.github.leapbound.yc.action.service.YcFunctionExecuteRecordService;
import com.github.leapbound.yc.action.utils.mapstruct.YcFunctionExecuteRecordMapStruct;

/**
 * @author yamath
 * @since 2023/7/14 9:39
 */
@Service
public class YcFunctionExecuteRecordServiceImpl
        extends ServiceImpl<YcFunctionExecuteRecordMapper, YcFunctionExecuteRecord>
        implements YcFunctionExecuteRecordService {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionExecuteRecordServiceImpl.class);

    @Override
    public void saveFunctionExecuteRecord(FunctionExecuteRecordSaveRequest request) {
        try {
            YcFunctionExecuteRecord record = YcFunctionExecuteRecordMapStruct.INSTANCE.requestToMapper(request);
            int row = baseMapper.insert(record);
        } catch (Exception ex) {
            logger.error("insert into [yc_function_execute_record] error, request = {}", request, ex);
        }
    }
}
