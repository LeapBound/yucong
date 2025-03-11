package com.github.leapbound.yc.hub.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.hub.consts.ProcessConsts;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.process.ProcessTaskDto;
import com.github.leapbound.yc.hub.service.ProcessService;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Fred Gu
 * @date 2024-12-04 10:27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FormService formService;

    /**
     * start process and return processInstanceId
     *
     * @param userId
     * @return String processInstanceId
     */
    @Override
    public String startProcess(String processKey, String userId, Map<String, Object> startFormVariables) {
        // 保持原有代码不变
        Task taskReturn = queryCurrentTask(userId, null);
        if (taskReturn == null) {
            log.info("user {} current no task", userId);
            // find process definition
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processKey)
                    .latestVersion()
                    .singleResult();

            // start process instance
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, "businessKey", startFormVariables);
            return processInstance.getProcessInstanceId();
        }

        return null;
    }

    /**
     * query current task by businessKey
     *
     * @return
     */
    private Task queryCurrentTask(String processInstanceId, String businessKey) {
        // get task
        TaskQuery taskQuery = taskService.createTaskQuery();

        if (StrUtil.isEmptyIfStr(businessKey)) {
            taskQuery = taskQuery.processInstanceBusinessKey(businessKey);
        } else if (StrUtil.isEmptyIfStr(businessKey)) {
            taskQuery = taskQuery.processInstanceId(processInstanceId);
        } else {
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
//        List<FormField> formFields = formService.getTaskFormData(currentTaskId).getFormFields();
//        List<ProcessStepInputForm> list = new ArrayList<>();
//        for (FormField formField : formFields) {
//            ProcessStepInputForm processStepInputForm = new ProcessStepInputForm();
//            processStepInputForm.setId(formField.getId());
//            processStepInputForm.setLabel(formField.getLabel());
//            processStepInputForm.setType(formField.getTypeName());
//            list.add(processStepInputForm);
//        }
        // current task variables
//        List<TaskProperties> taskProperties = getTaskProperties(processInstanceId, currentTaskId);

        return task;
    }

    /**
     * complete task with input variables by taskId
     *
     * @param taskId
     * @param taskInputVariable
     * @return
     */
    @Override
    public void completeTask(String taskId, Map<String, Object> taskInputVariable) {
        // task not found
        List<Task> currentTaskList = taskService.createTaskQuery().taskId(taskId).active().list();
        if (currentTaskList == null || currentTaskList.isEmpty()) {
        }
        // complete current task
        taskService.completeWithVariablesInReturn(taskId, null, true);
    }

    /**
     * fill task variables
     *
     * @param inputFormList
     * @param arguments
     * @return
     */
    private Map<String, Object> fillCurrentForm(List<Object> inputFormList, JSONObject arguments) {
        Map<String, Object> result = Maps.newHashMap();
        for (Object it : inputFormList) {
            String fieldName = "it.getId()";
            switch (it.getClass().getName()) {
                case "string":
                    result.put(fieldName, arguments.getString(fieldName));
                    break;
                case "boolean":
                    result.put(fieldName, arguments.getBoolean(fieldName));
                    break;
                case "date":
                    result.put(fieldName, arguments.getString(fieldName));
                    break;
                case "long":
                    result.put(fieldName, arguments.getLong(fieldName));
                    break;
                case "enum":
                    result.put(fieldName, arguments.getString(fieldName));
                    break;
                default:
                    result.put(fieldName, arguments.get(fieldName));
                    break;
            }
        }
        return result;
    }

    /**
     * get process variable by processInstanceId
     *
     * @param processInstanceId
     * @return
     */
    private JSONObject getProcessVariable(String processInstanceId) {
        return null;
    }

    /**
     * query current task when preview task completed
     *
     * @param userId
     * @return
     */
    private JSONObject nextForm(String userId) {
        return null;
    }

    @Override
    public ProcessTaskDto queryNextTask(String accountId) {
        return null;
    }

    @Override
    public String getProcessTaskRemind(String accountId, ProcessTaskDto currentTask, FunctionExecResultDto functionExecuteResult) {
        return "";
    }

    /**
     * 将TaskReturn转换为ProcessTaskDto
     *
     * @param taskReturn
     * @return
     */
    private ProcessTaskDto convertTaskReturnToProcessTaskDto(Object taskReturn) {
        if (taskReturn == null) {
            return null;
        }

        ProcessTaskDto processTaskDto = new ProcessTaskDto();

        return processTaskDto;
    }

    @Override
    public void deleteProcess(String processInstanceId) {
    }

    @Override
    public void inputProcessVariable(String processInstanceId, String businessKey, Map<String, Object> params) {
    }

    @Override
    public Set<String> loadTaskFunctionOptions(ProcessTaskDto task, boolean showRemind) {
        Map<String, String> showVariableMap = getTaskProperty(task, ProcessConsts.TASK_SHOW_VARIABLE);
        if (showVariableMap != null) {
            String showVariable = showVariableMap.get("name");

            switch (showVariableMap.get("type")) {
                case "set":
                    // 实现Set类型的处理逻辑
                    return getSetTypeOptions(task, showVariable);
                case "list":
                    // 实现List类型的处理逻辑
                    return getListTypeOptions(task, showVariable);
                case "map":
                    // 实现Map类型的处理逻辑
                    return getMapTypeOptions(task, showVariable);
                default:
                    log.warn("Unsupported variable type: {}", showVariableMap.get("type"));
                    break;
            }
        }

        return null;
    }

    /**
     * 获取Set类型的选项
     */
    private Set<String> getSetTypeOptions(ProcessTaskDto task, String variableName) {
        // 实现获取Set类型选项的逻辑
        // 这里需要根据实际情况实现
        return null;
    }

    /**
     * 获取List类型的选项
     */
    private Set<String> getListTypeOptions(ProcessTaskDto task, String variableName) {
        // 实现获取List类型选项的逻辑
        // 这里需要根据实际情况实现
        return null;
    }

    /**
     * 获取Map类型的选项
     */
    private Set<String> getMapTypeOptions(ProcessTaskDto task, String variableName) {
        // 实现获取Map类型选项的逻辑
        // 这里需要根据实际情况实现
        return null;
    }

    @Override
    public String getTaskFunction(ProcessTaskDto task) {
        return getTaskProperty(task, ProcessConsts.TASK_FUNCTION_UUID);
    }

    private <T> T getTaskProperty(ProcessTaskDto task, String name) {
        if (task == null) {
            return null;
        }

        AtomicReference<T> type = new AtomicReference<>();
        task.getTaskProperties().stream()
                .filter(property -> {
                    String propertyName = (String) property.get("name");
                    return propertyName.equals(name);
                })
                .findFirst()
                .ifPresent(property -> type.set((T) property.get("type")));
        return type.get();
    }
}
