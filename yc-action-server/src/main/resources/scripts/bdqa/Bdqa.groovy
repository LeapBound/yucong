package scripts.bdqa

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.CamundaService
import com.github.leapbound.yc.action.func.groovy.RequestAuth
import com.github.leapbound.yc.action.func.groovy.ResponseVo
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
@Field static String getSmsRecordPath = '/geex-ops-web/task/getSmsRecord'

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

    switch (method) {
        case 'start_ticket':
            result = startTicket(arguments)
            break
        case 'get_sms_record':
            result = getSmsRecord(arguments)
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
    String start = args.containsKey('start') ? args.getString('start') : ''
    String end = args.containsKey('end') ? args.getString('end') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    int page = args.containsKey('page') ? args.getIntValue('page') : 1
    int rows = args.containsKey('rows') ? args.getIntValue('rows') : 1
    def params = ['mobile': mobile, 'start': start, 'end': end, 'appId': appId, 'page': page, 'rows': rows]
    RequestAuth requestAuth = Alpha.setLoginRequestAuth(alphaUrl)
    def response = Alpha.doGetWithLogin(alphaUrl, getSmsRecordPath, params, requestAuth, 1)
    if (response == null) {
        logger.error("[get_sms_record]请求无反应")
        return ResponseVo.makeFail(9999, '[get_sms_record]没有反应')
    }
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
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
        logger.error("[get_sms_record]请求失败: status:{}, {}", response.getStatus(), response.body())
        return ResponseVo.makeFail(response.getStatus(), response.body())
    }
}