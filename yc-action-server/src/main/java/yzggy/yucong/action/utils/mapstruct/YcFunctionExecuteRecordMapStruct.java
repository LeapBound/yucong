package yzggy.yucong.action.utils.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import yzggy.yucong.action.entities.YcFunctionExecuteRecord;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRecordSaveRequest;

/**
 * @author yamath
 * @since 2023/7/14 9:52
 */
@Mapper
public interface YcFunctionExecuteRecordMapStruct {

    YcFunctionExecuteRecordMapStruct INSTANCE = Mappers.getMapper(YcFunctionExecuteRecordMapStruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "executeTime", source = "executeTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "resultTime", source = "resultTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    YcFunctionExecuteRecord requestToMapper(FunctionExecuteRecordSaveRequest request);
}
