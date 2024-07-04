package com.github.leapbound.yc.camunda.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.leapbound.yc.camunda.model.bo.ProcessStartRequest;
import com.github.leapbound.yc.camunda.model.bo.ProcessVariablesRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskCompleteRequest;
import com.github.leapbound.yc.camunda.model.bo.TaskFindRequest;
import com.github.leapbound.yc.camunda.model.vo.*;
import com.github.leapbound.yc.camunda.service.BusinessCamundaService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yamath
 * @date 2023/11/16 15:10
 */
@Service
public class BusinessCamundaServiceImpl implements BusinessCamundaService {

    private final RepositoryService repositoryService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final FormService formService;

    public BusinessCamundaServiceImpl(RepositoryService repositoryService,
                                      RuntimeService runtimeService,
                                      TaskService taskService,
                                      FormService formService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.formService = formService;
    }

    @Override
    public R<?> startProcess(ProcessStartRequest processStartRequest) {
        String key = processStartRequest.getKey();
        String processKey = processStartRequest.getProcessKey();
        String businessKey = processStartRequest.getBusinessKey();
        Map<String, Object> startFormVariables = processStartRequest.getStartFormVariables();
        // find process definition
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();
        if (processDefinition == null || StrUtil.isEmptyIfStr(processDefinition.getId())) {
            return R.error(9901, "No process definition [" + processKey + "] found.");
        }
        // start process instance
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, businessKey, startFormVariables);
        if (processInstance == null) {
            return R.error(9902, "Process start error. [" + processKey + "], [" + businessKey + "]");
        }
        String processInstanceId = processInstance.getProcessInstanceId();
        return R.ok(processInstanceId);
    }

    @Override
    public R<?> startProcessWithReturnTask(ProcessStartRequest processStartRequest) {
        R<?> ret = startProcess(processStartRequest);
        if (!R.isOk(ret)) {
            return ret;
        }
        String processInstanceId = (String) ret.getData();
        String businessKey = processStartRequest.getBusinessKey();
        // get task
        TaskReturn taskReturn = getCurrentTask(processInstanceId, businessKey);
        if (taskReturn == null) {
            return R.error(9903, "No task found, process instance [" + processInstanceId + "], business key [" + businessKey + "]");
        }
        //
        ProcessStep processStep = ProcessStep.builder().processInstanceId(processInstanceId)
                .currentTaskId(taskReturn.getTaskId())
                .currentStep(taskReturn.getTaskName())
                .task(taskReturn)
                .build();
        return R.ok(processStep);
    }

    @Override
    public R<?> findCurrentTask(TaskFindRequest taskFindRequest) {
        String processInstanceId = taskFindRequest.getProcessInstanceId();
        String businessKey = taskFindRequest.getBusinessKey();
        // get task
        TaskReturn taskReturn = getCurrentTask(processInstanceId, businessKey);
        if (taskReturn == null) {
            return R.error(9903, "No task found, process instance [" + processInstanceId + "]");
        }
        return R.ok(taskReturn);
    }

    @Override
    public R<Void> completeTask(TaskCompleteRequest taskCompleteRequest) {
        String key = taskCompleteRequest.getKey();
        String processInstanceId = taskCompleteRequest.getProcessInstanceId();
        String businessKey = taskCompleteRequest.getBusinessKey();
        String currentTaskId = taskCompleteRequest.getTaskId(); // current task id
        Map<String, Object> taskInputVariables = taskCompleteRequest.getTaskInputVariables();
        if (StrUtil.isEmptyIfStr(currentTaskId)) {
            TaskReturn taskReturn = getCurrentTask(processInstanceId, businessKey);
            if (taskReturn == null) {
                return R.error(9903, "No task found, process instance [" + processInstanceId + "]");
            }
            currentTaskId = taskReturn.getTaskId();
        }
        // task not found
        List<Task> currentTaskList = taskService.createTaskQuery().taskId(currentTaskId).active().list();
        if (currentTaskList == null || currentTaskList.isEmpty()) {
            return R.error(9904, "Cannot find task with id [" + currentTaskId + "], task is null.");
        }
        // complete current task
        taskService.completeWithVariablesInReturn(currentTaskId, taskInputVariables, true);
        return R.ok(null, "Task completed");
    }

    @Override
    public R<?> completeTaskWithReturn(TaskCompleteRequest taskCompleteRequest) {
        String key = taskCompleteRequest.getKey();
        String processInstanceId = taskCompleteRequest.getProcessInstanceId();
        String businessKey = taskCompleteRequest.getBusinessKey();
        // complete task
        R<Void> ret = completeTask(taskCompleteRequest);
        if (!R.isOk(ret)) {
            return ret;
        }
        // all process done
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (processInstance == null || tasks == null || tasks.isEmpty()) {
            ProcessStep processStep = ProcessStep.builder()
                    .processInstanceId(processInstanceId)
                    .currentStep("Process done")
                    .build();
            return R.ok(processStep);
        }
        // find next task
        TaskReturn taskReturn = getCurrentTask(processInstanceId, businessKey);
        if (taskReturn == null) {
            return R.error(9903, "No task found, process instance [" + processInstanceId + "]");
        }
        //
        ProcessStep processStep = ProcessStep.builder()
                .processInstanceId(processInstanceId)
                .currentTaskId(taskReturn.getTaskId())
                .currentStep(taskReturn.getTaskName())
                .task(taskReturn)
                .build();
        // get next task
        return R.ok(processStep);
    }

    @Override
    public R<?> cancelActivityToSpecify(TaskCompleteRequest taskCompleteRequest) {
        String processInstanceId = taskCompleteRequest.getProcessInstanceId();
        String businessKey = taskCompleteRequest.getBusinessKey();
        TaskReturn taskReturn = getCurrentTask(processInstanceId, businessKey);
        if (taskReturn == null) {
            return R.error(9903, "No task found, process instance [" + processInstanceId + "]");
        }

        // jump to specific activity
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelAllForActivity(taskReturn.getActivityId())
                .startBeforeActivity(taskCompleteRequest.getActivityId())
                .execute();
        taskReturn = getCurrentTask(processInstanceId, businessKey);
        if (taskReturn == null) {
            return R.error(9903, "No task found, process instance [" + processInstanceId + "]");
        }
        return R.ok(taskReturn);
    }

    @Override
    public R<?> inputProcessVariables(ProcessVariablesRequest processVariablesRequest) {
        String processInstanceId = processVariablesRequest.getProcessInstanceId();
        String businessKey = processVariablesRequest.getBusinessKey();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
//                .processInstanceBusinessKey(businessKey)
                .active()
                .singleResult();
        if (processInstance == null) {
            return R.error(9905, "No process instance active, process instance [" + processInstanceId + "]");
        }
        Map<String, Object> inputVariables = processVariablesRequest.getInputVariables();
        if (inputVariables != null && !inputVariables.isEmpty()) {
            for (String key : inputVariables.keySet()) {
                runtimeService.setVariable(processInstanceId, key, inputVariables.get(key));
            }
        }
        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
        return R.ok(variables);
    }

    @Override
    public R<?> getProcessVariables(ProcessVariablesRequest processVariablesRequest) {
        String processInstanceId = processVariablesRequest.getProcessInstanceId();
        String businessKey = processVariablesRequest.getBusinessKey();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
//                .processInstanceBusinessKey(businessKey)
                .active()
                .singleResult();
        if (processInstance == null) {
            return R.error(9905, "No process instance active, process instance [" + processInstanceId + "]");
        }
        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
        return R.ok(variables);
    }

    @Override
    public R<?> inputTaskVariablesLocal(TaskCompleteRequest taskCompleteRequest) {
        String processInstanceId = taskCompleteRequest.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
//                .processInstanceBusinessKey(businessKey)
                .active()
                .singleResult();
        if (processInstance == null) {
            return R.error(9905, "No process instance active, process instance [" + processInstanceId + "]");
        }
        String taskId = taskCompleteRequest.getTaskId();
        Map<String, Object> inputVariables = taskCompleteRequest.getTaskInputVariables();
        if (inputVariables != null && !inputVariables.isEmpty()) {
            taskService.setVariablesLocal(taskId, inputVariables);
        }
        Map<String, Object> variables = taskService.getVariablesLocal(taskId);
        return R.ok(variables);
    }

    @Override
    public R<?> getTaskVariablesLocal(TaskCompleteRequest taskCompleteRequest) {
        String processInstanceId = taskCompleteRequest.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
//                .processInstanceBusinessKey(businessKey)
                .active()
                .singleResult();
        if (processInstance == null) {
            return R.error(9905, "No process instance active, process instance [" + processInstanceId + "]");
        }
        String taskId = taskCompleteRequest.getTaskId();
        Map<String, Object> variables = taskService.getVariablesLocal(taskId);
        return R.ok(variables);
    }

    @Override
    public R<Void> deleteProcessInstance(String processInstanceId, String reason) {
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        return R.ok(null);
    }

    private TaskReturn getCurrentTask(String processInstanceId, String businessKey) {

        if (StrUtil.isEmptyIfStr(processInstanceId) && StrUtil.isEmptyIfStr(businessKey)) {
            return null;
        }
        // get task
        TaskQuery taskQuery = taskService.createTaskQuery();

        if (StrUtil.isEmptyIfStr(processInstanceId)) {
//            taskQuery = taskQuery.processInstanceBusinessKey(businessKey).active();
            taskQuery = taskQuery.processInstanceBusinessKey(businessKey);
        } else if (StrUtil.isEmptyIfStr(businessKey)) {
//            taskQuery = taskQuery.processInstanceId(processInstanceId).active();
            taskQuery = taskQuery.processInstanceId(processInstanceId);
        } else {
//            taskQuery = taskQuery.processInstanceId(processInstanceId).processInstanceBusinessKey(businessKey).active();
            taskQuery = taskQuery.processInstanceId(processInstanceId).processInstanceBusinessKey(businessKey);
        }
        Task task = taskQuery.singleResult();
        if (task == null || StrUtil.isEmptyIfStr(task.getId())) {
            return null;
        }
        // current task id
        String currentTaskId = task.getId();
        String taskName = task.getName();
        String activityId = task.getTaskDefinitionKey();
        if (StrUtil.isEmptyIfStr(processInstanceId)) {
            processInstanceId = task.getProcessInstanceId();
        }
        // current input form
        List<FormField> formFields = formService.getTaskFormData(currentTaskId).getFormFields();
        List<ProcessStepInputForm> list = new ArrayList<>();
        for (FormField formField : formFields) {
            ProcessStepInputForm processStepInputForm = new ProcessStepInputForm();
            processStepInputForm.setId(formField.getId());
            processStepInputForm.setLabel(formField.getLabel());
            processStepInputForm.setType(formField.getTypeName());
            list.add(processStepInputForm);
        }
        // current task variables
        List<TaskProperties> taskProperties = getTaskProperties(processInstanceId, currentTaskId);
        //
        return new TaskReturn(processInstanceId, currentTaskId, taskName, activityId, list, taskProperties);
    }

    private List<TaskProperties> getTaskProperties(String processInstanceId, String taskId) {
        List<TaskProperties> taskProperties = new ArrayList<>();
        Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
        //
        Map<String, Object> taskVariables = taskService.getVariables(taskId);
        for (String key : taskVariables.keySet()) {
            if (processVariables.containsKey(key)) {
                continue;
            }
            TaskProperties properties = new TaskProperties();
            properties.setName(key);
            properties.setType(taskVariables.get(key));
            taskProperties.add(properties);
        }
        return taskProperties;
    }
}
