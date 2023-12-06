package yzggy.yucong.action.service;

import com.baomidou.mybatisplus.extension.service.IService;
import yzggy.yucong.action.entities.YcFunctionManage;
import yzggy.yucong.action.model.dto.YcFunctionManageDto;
import yzggy.yucong.action.model.vo.ResponseVo;
import yzggy.yucong.action.model.vo.request.FunctionMethodSaveRequest;

import java.util.List;

/**
 * @author yamath
 * @since 2023/7/11 10:13
 */
public interface YcFunctionManageService extends IService<YcFunctionManage> {

    ResponseVo<Void> saveFunctionManage(FunctionMethodSaveRequest request);

    ResponseVo<Void> updateFunctionManage(FunctionMethodSaveRequest request);

    ResponseVo<Void> deleteFunctionManage(String functionName,
                                          String userName);

    List<YcFunctionManageDto> getFunctionManageDtoList(List<String> roleIdList);
}
