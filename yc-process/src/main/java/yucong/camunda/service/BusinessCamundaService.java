package yucong.camunda.service;

import yucong.camunda.model.bo.ProcessStartRequest;
import yucong.camunda.model.bo.ProcessVariablesRequest;
import yucong.camunda.model.bo.TaskCompleteRequest;
import yucong.camunda.model.bo.TaskFindRequest;
import yucong.camunda.model.vo.R;

/**
 * @author yamath
 * @since 2023/11/16 14:54
 */
public interface BusinessCamundaService {

    /**
     * 发起一个流程
     *
     * @param processStartRequest 启动流程请求参数
     * @return 流程 id
     */
    R<?> startProcess(ProcessStartRequest processStartRequest);

    /**
     * 发起一个流程并返回当前 step
     *
     * @param processStartRequest 启动流程请求参数
     * @return 流程
     */
    R<?> startProcessWithReturnTask(ProcessStartRequest processStartRequest);

    /**
     * 查找当前 task
     *
     * @param taskFindRequest task 查询请求
     * @return task 信息
     */
    R<?> findCurrentTask(TaskFindRequest taskFindRequest);

    /**
     * 完成当前 task
     *
     * @param taskCompleteRequest 完成 task 请求参数
     * @return void
     */
    R<Void> completeTask(TaskCompleteRequest taskCompleteRequest);

    /**
     * 完成当前 task 并进入下一步
     *
     * @param taskCompleteRequest 完成 task 请求参数
     * @return 流程
     */
    R<?> completeTaskWithReturn(TaskCompleteRequest taskCompleteRequest);

    R<?> cancelActivityToSpecify(TaskCompleteRequest taskCompleteRequest);

    R<?> inputProcessVariables(ProcessVariablesRequest processVariablesRequest);

    R<?> getProcessVariables(ProcessVariablesRequest processVariablesRequest);
}
