package scripts.bdqa

import cn.hutool.core.util.StrUtil
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
import scripts.general.GeneralMethods

/**
 *
 * @author yamath
 * @since 2024/5/24 17:28
 */
@Field static Logger logger = LoggerFactory.getLogger('scripts.bdqa.Bdqa');
@Field static String alphaUrl = ''
@Field static String hubUrl = ''
@Field static String getSmsRecordPath = '/geex-ops-web/task/getSmsRecord'
@Field static String noticeHubGroupPath = '/geex-smart-robot/yc-hub/servicer/switch/group'

execBdqaMethod(method, arguments)

static def execBdqaMethod(String method, String arguments) {
    JSONObject result = new JSONObject()
    // check arguments
    if (arguments == null || arguments.isEmpty()) {
        result.put('错误', '没有提供必要的信息')
        return result
    }
    // get external args
    alphaUrl = GeneralMethods.getExternal(arguments).get('alphaUrl')
    hubUrl = GeneralMethods.getExternal(arguments).get('hubUrl')

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
        case 'notice_hub_method':
            result = GeneralMethods.noticeHubMethod(arguments)
            break
        default:
            break
    }
    return result
}

// Enum
// question_verification_code

static def startTicket(String arguments) {
    JSONObject result = new JSONObject()
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String externalId = args.containsKey('externalId') ? args.getString('externalId') : ''
    String botId = args.containsKey('botid') ? args.getString('botid') : ''
    def startFormVariables = ['accountId': userId, 'botId': botId, 'externalId': externalId, 'question': 'question_verification_code']
    // process key = 'Process_bd_qa'
    String processInstanceId = CamundaService.startProcess('Process_bd_qa', userId, startFormVariables)
    //
    logger.info('{},{}, start_ticket', userId, externalId)
    return ResponseVo.makeSuccess(result)
}

static def getSmsRecord(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    if (StrUtil.isEmpty(mobile)) {
        return ResponseVo.makeFail(9999, '手机号不能为空')
    }
    // params
    String start = args.containsKey('start') ? args.getString('start') : ''
    String end = args.containsKey('end') ? args.getString('end') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    int page = args.containsKey('page') ? args.getIntValue('page') : 1
    int rows = args.containsKey('rows') ? args.getIntValue('rows') : 1
    //
    def params = ['mobile': mobile, 'start': start, 'end': end, 'appId': appId, 'page': page, 'rows': rows]
    // check task
    def taskId = checkTask(args)
    if (taskId == null) {
        return ResponseVo.makeFail(9999, '没有任务')
    }
    //
    try {
        RequestAuth requestAuth = Alpha.setLoginRequestAuth(alphaUrl)
        def response = Alpha.doGetWithLogin(alphaUrl, getSmsRecordPath, params, requestAuth, 1)
        if (response == null) {
            logger.error("[get_sms_record]请求无反应")
            return ResponseVo.makeFail(9999, '[get_sms_record]没有反应')
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
                    return ResponseVo.makeSuccess(smsReport.getString('desc'))
                } else {
                    return ResponseVo.makeSuccess('短信发送失败')
                }
            } else {
                return ResponseVo.makeSuccess('没有查询到短信发送记录')
            }
        } else {
            logger.error("[get_sms_record]请求失败: status:{}, {}", response.getStatus(), response.body())
            return ResponseVo.makeFail(response.getStatus(), response.body())
        }
    } finally {
        doCompleteTask(args, taskId, null)
    }
}

static def checkProblemSolved(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String taskId = checkTask(args)
    if (taskId == null) {
        return ResponseVo.makeFail(9999, '没有任务')
    }
    doCompleteTask(args, taskId, ['solved'])
    return ResponseVo.makeSuccess(null)
}

static def humanProcess(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String serviceGroupTag = args.containsKey('service_group_tag') ? args.getString('service_group_tag') : ''
    String accountId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String externalId = args.containsKey('externalId') ? args.getString('externalId') : ''
    //
    def noticeData = new JSONObject() {
        {
            put('accountId', accountId)
            put('externalUserId', externalId)
            put('serviceGroup', serviceGroupTag)
            put('noticeHubUrl', hubUrl)
            put('noticeHubPath', noticeHubGroupPath)
        }
    }
    Map<String, Object> afterFunctionMap = noticeMap(noticeData)
    return new JSONObject() {
        {
            put('afterFunction', afterFunctionMap)
        }
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

/**
 * complete task with input variables
 * @param args arguments
 * @param taskId current taskId
 * @param inputKeys keys of input variables
 * @return
 */
static def doCompleteTask(JSONObject args, String taskId, List<String> inputKeys) {
    Map<String, Object> map = new HashMap<>()
    if (inputKeys != null && !inputKeys.isEmpty()) {
        for (String inputKey : inputKeys) {
            String inputValue = args.containsKey(inputKey) ? args.getString(inputKey) : ''
            if (!StrUtil.isEmpty(inputValue)) {
                map.put(inputKey, inputValue)
            }
        }
    }
    CamundaService.completeTask(taskId, map)
}

static def noticeMap(JSONObject noticeData) {
    return new HashMap<String, Object>() {
        {
            put('notice_hub_method', noticeData)
        }
    }
}