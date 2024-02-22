package yucong.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import yucong.camunda.entities.ProcessFunctionManage;
import yucong.camunda.model.bo.ProcessFunctionSaveRequest;
import yucong.camunda.model.vo.R;

/**
 * @author yamath
 * @since 2023/11/24 11:28
 */
public interface FunctionManageService extends IService<ProcessFunctionManage> {

    R<?> saveProcessFunctionManage(ProcessFunctionSaveRequest processFunctionSaveRequest);

    R<?> getProcessFunction(String processKey);
}
