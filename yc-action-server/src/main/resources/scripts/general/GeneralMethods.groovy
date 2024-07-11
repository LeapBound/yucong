package scripts.general

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.CamundaService
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
// 一些脚本中可能需要的外部参数 key
@Field static List<String> externalKeys = ['frontUrl', 'hubUrl', 'alphaUrl', 'gonggongUrl', 'qiguanUrl', 'zhangwuUrl', 'zijinUrl', 'dingdanUrl']

/**
 * 从传入的参数中提取需要的 external args
 * @param arguments 传入的参数
 * @return
 */
static def getExternal(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    for (String key : externalKeys) {
        if (args.containsKey(key)) {
            String value = args.getString(key)
            result.put(key, value)
        }
    }
    return result
}

/**
 * 从传入的参数中移除不需要的 external args
 * @param args
 * @param removeKeys
 * @return
 */
static def removeExternal(JSONObject args, List<String> removeKeys) {
    JSONObject result = args
    for (String key : removeKeys) {
        if (result.containsKey(key)) {
            result.remove(key)
        }
    }
    return result
}

/**
 * 通知 yc-hub 的通用方法
 * @param arguments 传入的参数
 * @return
 */
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
            Object inputValue = args.containsKey(inputKey) ? args.get(inputKey) : null
            if (inputValue != null) {
                map.put(inputKey, inputValue)
            }
        }
    }
    CamundaService.completeTask(taskId, map)
}