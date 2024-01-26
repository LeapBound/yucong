package geex.architecture.guts.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import geex.architecture.guts.camunda.entities.ProcessFunctionManage;
import geex.architecture.guts.camunda.model.bo.ProcessFunctionSaveRequest;
import geex.architecture.guts.camunda.model.vo.ProcessFunction;
import geex.architecture.guts.camunda.model.vo.R;

/**
 * @author yamath
 * @since 2023/11/24 11:28
 */
public interface FunctionManageService extends IService<ProcessFunctionManage> {

    R<?> saveProcessFunctionManage(ProcessFunctionSaveRequest processFunctionSaveRequest);

    R<?> getProcessFunction(String processKey);
}
