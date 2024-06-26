package com.github.leapbound.yc.camunda.controller;

import com.github.leapbound.yc.camunda.model.bo.ProcessStartRequest;
import com.github.leapbound.yc.camunda.model.bo.ProcessVariablesRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskCompleteRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskFindRequest;
import com.github.leapbound.yc.camunda.model.vo.R;
import com.github.leapbound.yc.camunda.service.BusinessCamundaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author yamath
 * @date 2023/11/16 11:34
 */
@Slf4j
@RestController
@RequestMapping("/business")
public class BusinessController {

    private final BusinessCamundaService businessCamundaService;

    public BusinessController(BusinessCamundaService businessCamundaService) {
        this.businessCamundaService = businessCamundaService;
    }

    @PostMapping("/process/start")
    public R<?> processStart(@RequestBody ProcessStartRequest processStartRequest) {
        log.info("business start process, request: {}", processStartRequest);
        return businessCamundaService.startProcess(processStartRequest);
    }

    @PostMapping("/process/delete")
    public R<Void> processDelete(@RequestParam("processInstanceId") String processInstanceId,
                                 @RequestParam(value = "reason", required = false) String reason) {
        return businessCamundaService.deleteProcessInstance(processInstanceId, reason);
    }

    @PostMapping("/process/startWithReturn")
    public R<?> processStartWithReturn(@RequestBody ProcessStartRequest processStartRequest) {
        log.info("business start process, request: {}", processStartRequest);
        return businessCamundaService.startProcessWithReturnTask(processStartRequest);
    }

    @PostMapping("/process/variables/input")
    public R<?> inputProcessVariables(@RequestBody ProcessVariablesRequest processVariablesRequest) {
        log.info("business input process variables, request: {}", processVariablesRequest);
        return businessCamundaService.inputProcessVariables(processVariablesRequest);
    }

    @PostMapping("/process/variables")
    public R<?> getProcessVariables(@RequestBody ProcessVariablesRequest processVariablesRequest) {
        log.info("business get process variables, request: {}", processVariablesRequest);
        return businessCamundaService.getProcessVariables(processVariablesRequest);
    }

    @PostMapping("/task")
    public R<?> task(@RequestBody TaskFindRequest taskFindRequest) {
        log.info("business find task, request: {}", taskFindRequest);
        return businessCamundaService.findCurrentTask(taskFindRequest);
    }

    @PostMapping("/task/complete")
    public R<?> taskComplete(@RequestBody TaskCompleteRequest taskCompleteRequest) {
        log.info("business complete task, request: {}", taskCompleteRequest);
        return businessCamundaService.completeTask(taskCompleteRequest);
    }

    @PostMapping("/task/completeWithReturn")
    public R<?> taskCompleteWithReturn(@RequestBody TaskCompleteRequest taskCompleteRequest) {
        log.info("business complete task, request: {}", taskCompleteRequest);
        return businessCamundaService.completeTaskWithReturn(taskCompleteRequest);
    }

    @PostMapping("/task/cancelToSpecify")
    public R<?> taskCancelToSpecify(@RequestBody TaskCompleteRequest taskCompleteRequest) {
        log.info("business cancel task and skip to, request: {}", taskCompleteRequest);
        return businessCamundaService.cancelActivityToSpecify(taskCompleteRequest);
    }
}
