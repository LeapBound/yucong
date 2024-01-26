package yzggy.yucong.action.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;
import yzggy.yucong.action.entities.YcFunctionRole;
import yzggy.yucong.action.model.vo.ResponseVo;
import yzggy.yucong.action.model.vo.YcFunctionRoleVo;
import yzggy.yucong.action.model.vo.request.FunctionRoleSaveRequest;

import java.util.List;

/**
 * @author yamath
 * @since 2023/7/11 10:13
 */
public interface YcFunctionRoleService extends IService<YcFunctionRole> {

    ResponseVo<Void> saveFunctionRole(FunctionRoleSaveRequest request);

    ResponseVo<Void> deleteFunctionRole(YcFunctionRoleVo vo);

    List<String> getAllFunctionRoleIdList();
}
