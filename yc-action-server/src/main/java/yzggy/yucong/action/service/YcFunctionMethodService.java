package yzggy.yucong.action.service;

import com.baomidou.mybatisplus.extension.service.IService;
import yzggy.yucong.action.entities.YcFunctionMethod;
import yzggy.yucong.action.model.dto.YcFunctionMethodDto;
import yzggy.yucong.action.model.vo.ResponseVo;
import yzggy.yucong.action.model.vo.request.FunctionMethodSaveRequest;

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
