package scripts.general

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.RestClient
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author yamath
 * @since 2024/6/20 11:26
 */

@Field static Logger logger = LoggerFactory.getLogger('scripts.general.GeneralMethods');
@Field static List<String> externalKeys = ['frontUrl', 'hubUrl', 'alphaUrl', 'gonggongUrl', 'qiguanUrl', 'zhangwuUrl', 'zijinUrl', 'dingdanUrl']

static def getExternal(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    externalKeys.each { String key ->
        if (args.containsKey(key)) {
            String value = args.getString(key)
            result.put(key, value)
        }
    }
    return result
}

static def removeExternal(JSONObject args, List<String> removeKeys) {
    JSONObject result = args
    removeKeys.each { String key ->
        if (result.containsKey(key)) {
            result.remove(key)
        }
    }
    return result
}

static def noticeHubMethod(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String noticeHubPath = args.containsKey('noticeHubPath') ? args.getString('noticeHubPath') : ''
    String noticeHubUrl = args.containsKey('noticeHubUrl') ? args.getString('noticeHubUrl') : ''
    List<String> removeKeys = ['noticeHubPath', 'noticeHubUrl']
    removeKeys.addAll(externalKeys)
    def params = removeExternal(args, removeKeys)
    //
    def response = RestClient.doPostWithBody(noticeHubUrl, noticeHubPath, params, null)
    if (!StrUtil.isEmpty(response.body())) {
        logger.info('noticeHub response {}', response.body())
    }
}