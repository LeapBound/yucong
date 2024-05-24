package scripts.bdqa

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.CamundaService
import com.github.leapbound.yc.action.func.groovy.ResponseVo
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author yamath
 * @since 2024/5/24 17:28
 */
@Field static Logger logger = LoggerFactory.getLogger('scripts.bdqa.Bdqa');

execBdqaMethod(method, arguments)

static def execBdqaMethod(String method, String arguments) {
    JSONObject result = new JSONObject()
    // check arguments
    if (arguments == null || arguments.isEmpty()) {
        result.put('错误', '没有提供必要的信息')
        return result
    }

    switch (method) {
        case 'start_ticket':
            result = startTicket(arguments)
            break
        default:
            break
    }
    return result
}

static def startTicket(String arguments) {
    JSONObject result = new JSONObject()
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String externalId = args.containsKey('externalId') ? args.getString('externalId') : ''
    String botId = args.containsKey('botid') ? args.getString('botid') : ''
    def startFormVariables = ['accountId': userId, 'botId': botId, 'externalId': externalId]
    // process key = 'Process_bd_qa'
    String processInstanceId = CamundaService.startProcess('Process_bd_qa', userId, startFormVariables)
    //
    logger.info('{},{}, start_ticket', userId, externalId)
    return ResponseVo.makeSuccess(result)
}

static def getTicket(String arguments) {

}