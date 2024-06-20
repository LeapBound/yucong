package scripts.general


import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

/**
 *
 * @author yamath
 * @since 2024/6/20 11:26
 */

static def getExternal(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String externalFrontUrl = args.containsKey('frontUrl') ? args.getString('frontUrl') : ''
    String externalHubUrl = args.containsKey('hubUrl') ? args.getString('hubUrl') : ''
    String externalAlphaUrl = args.containsKey('alphaUrl') ? args.getString('alphaUrl') : ''
    return ['frontUrl': externalFrontUrl, 'hubUrl': externalHubUrl, 'alphaUrl': externalAlphaUrl]
}
