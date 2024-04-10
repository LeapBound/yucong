package com.github.leapbound.yc.action.func.groovy

import cn.hutool.extra.spring.SpringUtil
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.camunda.model.bo.ProcessStartRequest
import com.github.leapbound.yc.camunda.model.bo.ProcessVariablesRequest
import com.github.leapbound.yc.camunda.model.bo.TaskCompleteRequest
import com.github.leapbound.yc.camunda.model.bo.TaskFindRequest
import com.github.leapbound.yc.camunda.model.vo.ProcessStep
import com.github.leapbound.yc.camunda.model.vo.ProcessStepInputForm
import com.github.leapbound.yc.camunda.model.vo.R
import com.github.leapbound.yc.camunda.model.vo.TaskReturn
import com.github.leapbound.yc.camunda.service.BusinessCamundaService
import com.google.common.collect.Maps
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author yamath
 * @since 2024/3/28 14:26
 *
 */
class CamundaService {

    static Logger logger = LoggerFactory.getLogger(CamundaService.class)

    static BusinessCamundaService businessCamundaService = SpringUtil.getBean(BusinessCamundaService.class)

    /**
     * start process and return processInstanceId
     * @param userId
     * @return String processInstanceId
     */
    static def startProcess(String processKey, String userId) {
        TaskReturn taskReturn = queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.info('user {} current no task', userId)
            ProcessStartRequest processStartRequest = new ProcessStartRequest()
            processStartRequest.setProcessKey(processKey)
            processStartRequest.setBusinessKey(userId)
            def r = businessCamundaService.startProcess(processStartRequest)
            if (R.isOk(r)) {
                return r.getData() as String
            } else {
                logger.error('start process error, {}', r.getMsg())
            }
            return null
        }
    }

    static def deleteProcess(String userId) {
        TaskReturn taskReturn = queryCurrentTask(userId)
        if (taskReturn != null) {
            String processInstanceId = taskReturn.getProcessInstanceId()
            businessCamundaService.deleteProcessInstance(processInstanceId, '')
        }
    }

    static def queryCurrentTask(String userId) {
        TaskFindRequest taskFindRequest = new TaskFindRequest()
        taskFindRequest.setBusinessKey(userId)
        def r = businessCamundaService.findCurrentTask(taskFindRequest)
        if (R.isOk(r)) {
            TaskReturn taskReturn = r.getData() as TaskReturn
            return taskReturn
        } else {
            logger.error('query current task error, {}', r.getMsg())
            return null
        }
    }

    static def completeTask(String taskId, Map<String, Object> taskInputVariable) {
        TaskCompleteRequest taskCompleteRequest = new TaskCompleteRequest()
        taskCompleteRequest.setTaskId(taskId)
        taskCompleteRequest.setTaskInputVariables(taskInputVariable)
        def r = businessCamundaService.completeTask(taskCompleteRequest)
        if (R.isOk(r)) {
            logger.info('task completed, {}', taskId)
        } else {
            logger.error('complete task error, {}', r.getMsg())
        }
    }

    static def completeTaskWithReturn(String processInstanceId, String taskId, Map<String, Object> taskInputVariable) {
        TaskCompleteRequest taskCompleteRequest = new TaskCompleteRequest()
        taskCompleteRequest.setProcessInstanceId(processInstanceId)
        taskCompleteRequest.setTaskId(taskId)
        taskCompleteRequest.setTaskInputVariables(taskInputVariable)
        def r = businessCamundaService.completeTaskWithReturn(taskCompleteRequest)
        if (R.isOk(r)) {
            logger.info('task completed, {}', taskId)
            return r.getData() as ProcessStep
        }
        logger.error('complete task error, {}', r.getMsg())
        return null
    }

    static def nextForm(String userId) {
        JSONObject result = new JSONObject()
        TaskReturn task = queryCurrentTask(userId)
        if (task == null) {
            result.put('result', 'Please wait a moment ...')
        }
        return result
    }

    static def fillCurrentForm(List<ProcessStepInputForm> inputFormList, JSONObject arguments) {
        Map<String, Object> result = Maps.newHashMap()
        inputFormList.each {
            def fieldName = it.getId()
            switch (it.getType()) {
                case 'string':
                    result.put(fieldName, arguments.getString(fieldName))
                    break
                case 'boolean':
                    result.put(fieldName, arguments.getBoolean(fieldName))
                    break
                case 'date':
                    result.put(fieldName, arguments.getString(fieldName))
                    break
                case 'long':
                    result.put(fieldName, arguments.getLong(fieldName))
                    break
                case 'enum':
                    result.put(fieldName, arguments.getString(fieldName))
                    break
                default:
                    result.put(fieldName, arguments.get(fieldName))
                    break
            }
        }
        return result
    }

    static def getProcessVariable(String processInstanceId) {
        ProcessVariablesRequest processVariablesRequest = new ProcessVariablesRequest();
        processVariablesRequest.setProcessInstanceId(processInstanceId)
        def r = businessCamundaService.getProcessVariables(processVariablesRequest)
        if (R.isOk(r)) {
            return r.getData() as JSONObject
        }
        logger.error("get process variable error, {}", r.getMsg())
        return null
    }

    static def getTaskVariableLocal(String processInstanceId, String taskId) {
        TaskCompleteRequest taskCompleteRequest = new TaskCompleteRequest()
        taskCompleteRequest.setProcessInstanceId(processInstanceId)
        taskCompleteRequest.setTaskId(taskId)
        def r = businessCamundaService.getTaskVariablesLocal(taskCompleteRequest)
        if (R.isOk(r)) {
            return r.getData() as JSONObject
        }
        logger.error("get process variable local error, {}", r.getMsg())
        return null
    }

    static def setTaskVariableLocal(Map<String, Object> variablesLocal, String processInstanceId, String taskId) {
        TaskCompleteRequest taskCompleteRequest = new TaskCompleteRequest();
        taskCompleteRequest.setProcessInstanceId(processInstanceId)
        taskCompleteRequest.setTaskId(taskId)
        taskCompleteRequest.setTaskInputVariables(variablesLocal)
        def r = businessCamundaService.inputTaskVariablesLocal(taskCompleteRequest)
        if (R.isOk(r)) {
            return r.getData() as JSONObject
        }
        logger.error("set task variable local error, {}", r.getMsg())
        return null
    }
}
