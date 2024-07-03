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

static def getExternal(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String externalFrontUrl = args.containsKey('frontUrl') ? args.getString('frontUrl') : ''
    String externalHubUrl = args.containsKey('hubUrl') ? args.getString('hubUrl') : ''
    String externalAlphaUrl = args.containsKey('alphaUrl') ? args.getString('alphaUrl') : ''
    String gonggongUrl = args.containsKey('gonggongUrl') ? args.getString('gonggongUrl') : ''
    String qiguanUrl = args.containsKey('qiguanUrl') ? args.getString('qiguanUrl') : ''
    String zhangwuUrl = args.containsKey('zhangwuUrl') ? args.getString('zhangwuUrl') : ''
    String dingdanUrl = args.containsKey('dingdanUrl') ? args.getString('dingdanUrl') : ''
    String zijinUrl = args.containsKey('zijinUrl') ? args.getString('zijinUrl') : ''
    return ['frontUrl'   : externalFrontUrl, 'hubUrl': externalHubUrl, 'alphaUrl': externalAlphaUrl,
            'gonggongUrl': gonggongUrl, 'qiguanUrl': qiguanUrl, 'zhangwuUrl': zhangwuUrl,
            'dingdanUrl' : dingdanUrl, 'zijinUrl': zijinUrl]
}

static def noticeHubMethod(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String noticeHubPath = args.containsKey('noticeHubPath') ? args.getString('noticeHubPath') : ''
    String noticeHubUrl = args.containsKey('noticeHubUrl') ? args.getString('noticeHubUrl') : ''
    def response = RestClient.doPostWithBody(noticeHubUrl, noticeHubPath, args, null)
    if (!StrUtil.isEmpty(response.body())) {
        logger.info('noticeHub response {}', response.body())
    }
}