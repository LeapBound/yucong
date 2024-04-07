package scripts.camunda

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.RestClient
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author yamath
 * @since 2024/3/27 10:50
 */

@Field static Logger logger = LoggerFactory.getLogger("scripts.camunda.Camunda");
@Field static String camundaUrl = "http://localhost:8080"
@Field static String startProcessPath = "/geex-guts-camunda/business/process/start"

// start process
static def startProcess(String arguments) {
    logger.info('startProcess arguments')
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('userId') ? args.getString('userId') : ''
    String processKey = args.containsKey('processKey') ? args.getString('processKey') : ''
    def params = ['processKey': processKey, 'businessKey': userId]
    try {
        def response = RestClient.doPostWithBody(camundaUrl, startProcessPath, params, null)
        if (response == null) {
            logger.error("startProcess no response")
            return null
        }
        logger.info("startProcess response: {}", response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getString('data')
        }
    } catch (Exception ex) {
        logger.error("startProcess error,", ex)
    }
    return null
}

