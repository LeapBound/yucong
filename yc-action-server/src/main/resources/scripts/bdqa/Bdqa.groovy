package scripts.bdqa

import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpResponse
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.CamundaService
import com.github.leapbound.yc.action.func.groovy.RequestAuth
import com.github.leapbound.yc.action.func.groovy.ResponseVo
import com.github.leapbound.yc.camunda.model.vo.TaskReturn
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import scripts.alpha.Alpha
import scripts.general.GeneralCodes
import scripts.general.GeneralMethods

/**
 *
 * @author yamath
 * @since 2024/5/24 17:28
 */
@Field static Logger logger = LoggerFactory.getLogger('scripts.bdqa.Bdqa');
@Field static String gonggongUrl = ''
@Field static String qiguanUrl = ''
@Field static String getSmsRecordPath = '/geex-ops-web/task/getSmsRecord'
@Field static String updateOrderResultPath = '/geex-ops-web/api/updOrderResult'
@Field static String noticeHubGroupPath = '/geex-smart-robot/yc-hub/api/conversation/servicer/switch/group'
@Field static Map<String, Integer> orderResultMap = ['回退'    : 1,
                                                     '拒绝'    : 2,
                                                     '取消'    : 3,
                                                     '延迟取消': 6,
                                                     '审批中'  : 10,
                                                     '复合中'  : 11,
                                                     '已保存'  : 12]

execBdqaMethod(method, arguments)

static def execBdqaMethod(String method, String arguments) {
    JSONObject result = new JSONObject()
    // check arguments
    if (StrUtil.isEmptyIfStr(arguments)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供必要的信息')
    }
    // get external args
    gonggongUrl = GeneralMethods.getExternal(arguments).get('gonggongUrl')
    Alpha.alphaLoginUrl = gonggongUrl
    qiguanUrl = GeneralMethods.getExternal(arguments).get('qiguanUrl')

    switch (method) {
        case 'start_ticket':
            result = startTicket(arguments)
            break
        case 'get_sms_record':
            result = getSmsRecord(arguments)
            break
        case 'check_problem_solved':
            result = checkProblemSolved(arguments)
            break
        case 'human_process': // service task
            result = humanProcess(arguments)
            break
        case 'update_order_result':
            result = updateOrderResult(arguments)
            break
        case 'notice_hub_method':
            result = GeneralMethods.noticeHubMethod(arguments)
            break
        default:
            result = ResponseVo.makeFail(GeneralCodes.MISSING_EXEC_METHOD, '没有对应的执行方法')
            break
    }
    return result
}

// Enum
// question_verification_code

static def startTicket(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String externalId = args.containsKey('externalId') ? args.getString('externalId') : ''
    String botId = args.containsKey('botid') ? args.getString('botid') : ''
    Map<String, Object> startFormVariables = new HashMap() {
        {
            put('accountId', userId)
            put('botId', botId)
            put('externalId', externalId)
            put('question', 'question_verification_code')
        }
    }
    // process key = 'Process_bd_qa'
    String processInstanceId = CamundaService.startProcess('Process_bd_qa', userId, startFormVariables)
    //
    logger.info('{},{}, start_ticket', userId, externalId)
    return ResponseVo.makeSuccess(null)
}

static def getSmsRecord(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    if (StrUtil.isEmpty(mobile)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '手机号不能为空')
    }
    // params
    String start = args.containsKey('start') ? args.getString('start') : ''
    String end = args.containsKey('end') ? args.getString('end') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    int page = args.containsKey('page') ? args.getIntValue('page') : 1
    int rows = args.containsKey('rows') ? args.getIntValue('rows') : 1
    // check task
    String taskId = checkTask(args)
    if (taskId == null) {
        logger.warn('[get_sms_record] no current task')
//        return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '当前没有任务')
    }
    //
    Map<String, Object> params = new HashMap() {
        {
            put('mobile', mobile)
            put('start', start)
            put('end', end)
            put('appId', appId)
            put('page', page)
            put('rows', rows)
        }
    }
    //
    try {
        RequestAuth requestAuth = Alpha.setLoginRequestAuth()
        HttpResponse response = Alpha.doGetWithLogin(qiguanUrl, getSmsRecordPath, params, requestAuth, 1)
        if (response == null) {
            logger.error("[get_sms_record] no response")
            return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '取得短信记录没有响应，联系管理员')
        }
        if (response.isOk()) {
            logger.info("[get_sms_record]请求结果: {}", response.body())
            JSONObject jsonObject = JSON.parseObject(response.body())
            if (jsonObject.containsKey('rows') && !jsonObject.getJSONArray('rows').isEmpty()) {
                JSONObject smsReport = jsonObject.getJSONArray('rows').getJSONObject(0);
                if (StrUtil.isEmpty(smsReport.getString('desc'))) {
                    return ResponseVo.makeSuccess('短信已发送，但没有状态报告')
                }
                if (smsReport.getString('desc').contains('接收成功')) {
                    return ResponseVo.makeSuccess('短信发送结果:' + smsReport.getString('desc'))
                } else {
                    return ResponseVo.makeSuccess('短信发送失败')
                }
            } else {
                return ResponseVo.makeSuccess('没有查询到短信发送记录')
            }
        } else {
            logger.error('[get_sms_record] response status:{}, {}', response.getStatus(), response.body())
            return ResponseVo.makeFail(response.getStatus(), response.body())
        }
    } finally {
        if (taskId != null) {
            GeneralMethods.doCompleteTask(args, taskId, null)
        }
    }
}

static def checkProblemSolved(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String taskId = checkTask(args)
    if (taskId == null) {
        return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '当前没有任务')
    }
    GeneralMethods.doCompleteTask(args, taskId, ['solved'])
    return ResponseVo.makeSuccess(null)
}

static def humanProcess(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String serviceGroupTag = args.containsKey('service_group_tag') ? args.getString('service_group_tag') : ''
    String accountId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String externalUserId = args.containsKey('externalUserId') ? args.getString('externalUserId') : ''
    String openKfId = args.containsKey('openKfId') ? args.getString('openKfId') : ''
    JSONObject data = new JSONObject() {
        {
            put('accountId', accountId)
            put('externalUserId', externalUserId)
            put('serviceGroup', serviceGroupTag)
            put('serviceState', 3)
            put('openKfId', openKfId)
        }
    }
    //
    JSONObject noticeData = new JSONObject() {
        {
            put('data', data)
            put('noticeHubUrl', gonggongUrl)
            put('noticeHubPath', noticeHubGroupPath)
        }
    }
    Map<String, Object> afterFunctionMap = GeneralMethods.noticeMap(noticeData)
    return new JSONObject() {
        {
            put('afterFunction', afterFunctionMap)
        }
    }
}

static def updateOrderResult(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String accountId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String username = args.containsKey('username') ? args.getString('username') : accountId
    String orderResult = args.containsKey('orderResult') ? args.getString('orderResult') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    if (StrUtil.isEmptyIfStr(appId)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '订单号不能为空')
    }
    if (StrUtil.isEmptyIfStr(orderResult)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '要更改的状态不能为空')
    }
    def result = 0
    for (String key in orderResultMap.keySet()) {
        if (key.contains(orderResult)) {
            result = orderResultMap.get(key)
        }
    }
    if (result == 0) {
        return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_PARAMS_INVALID, '要更改的状态不支持: [' + orderResult + ']')
    }
    //
    Map<String, Object> params = ['appId': appId, 'username': username, 'result': result] as Map<String, Object>
    RequestAuth requestAuth = Alpha.setLoginRequestAuth()
    HttpResponse response = Alpha.doGetWithLogin(qiguanUrl, updateOrderResultPath, params, requestAuth, 1)
    if (response == null) {
        logger.error('[update_order_result] no response')
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '[小工具]更改订单状态没有响应')
    }
    if (response.isOk()) {
        logger.info('[update_order_result] result: {}', response.body())
        JSONObject jsonObject = JSON.parseObject(response.body())
        boolean success = jsonObject.getBooleanValue('success')
        if (success) {
            return ResponseVo.makeSuccess(jsonObject.get('result'))
        } else {
            return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, jsonObject.getString('errorMessage'))
        }
    } else {
        logger.error('[update_order_result] response status: {}, {}', response.getStatus(), response.body())
        return ResponseVo.makeFail(response.getStatus(), response.body())
    }
}

/**
 * check task exist and return taskId
 * @param args arguments
 * @return
 */
static def checkTask(JSONObject args) {
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
    if (taskReturn == null) {
        logger.error('getSmsRecord no current task, businessKey: {}', userId)
        return null
    }
    return taskReturn.getTaskId()
}