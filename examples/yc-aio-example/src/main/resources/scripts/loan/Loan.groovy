package scripts.loan

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.extra.spring.SpringUtil
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.RequestAuth
import com.github.leapbound.yc.action.func.groovy.RestClient
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate

/**
 *
 * @author yamath
 * @since 2024/3/26 9:42
 */
@Field static String frontUrl = 'https://beta.geexfinance.com'
@Field static String loginAppPath = '/front-api/geex_capp/v1/user/loginApp'
@Field static String getAppVerifyCodeCheckPath = '/front-api/geex_capp/v1/sms/getAppVerifyCodeCheck'
@Field static String preApplyPath = '/front-api/geex_capp/v1/order/preApply/'
@Field static String loadIdentityPath = '/front-api/geex_capp/v1/user/loadIdentity/'
@Field static String doIdCardOcrPath = '/front-api/geex_capp/v1/media/doIdCardOcr/'
@Field static String checkBankCardLimitPath = '/front-api/geex_capp/v1/order/checkBankCardLimit'
@Field static String checkOldIdentityPath = '/front-api/geex_capp/v1/user/checkOldIdentity/'
@Field static String checkProtocolPath = '/front-api/geex_capp/v1/user/checkProtocol/'
@Field static String submitProtocolPath = '/front-api/geex_capp/v1/user/submitProtocol/'
@Field static String submitIdentityPath = '/front-api/geex_capp/v1/user/submitIdentity/'
@Field static String submitApplyStepPath = '/front-api/geex_capp/v1/apply/submitApplyStep/'
@Field static String APP_TOKEN_KEY = 'yc.a.s.app.token.'
@Field static Logger logger = LoggerFactory.getLogger("scripts.loan.Loan");

execLoanMethod(method, arguments)

static def execLoanMethod(String method, String arguments) {
    JSONObject result = new JSONObject()
    // check arguments
    if (arguments == null || arguments.isEmpty()) {
        result.put('错误', '没有提供必要的信息')
        return result
    }
    switch (method) {
        case 'loginApp':
            result = JSON.parseObject(loginApp(arguments))
            break
        case 'getAppToken':
            result = JSON.parseObject(getAppToken(arguments))
            break
        case 'sendLoginSms':
            sendLoginSms(arguments)
            break
        case 'notifyUser':
            notifyUser(arguments)
            break
        case 'preApply':
            result = preApply(arguments)
            break
        case 'loadIdentity':
            result = loadIdentity(arguments)
            break
        case 'doIdCardOcr':
            result = doIdCardOcr(arguments)
            break
        case 'checkBankCardLimit':
            result = checkBankCardLimit(arguments)
            break
        case 'checkOldIdentity':
            result = checkOldIdentity(arguments)
            break
        case 'checkUserPayProtocol':
            result = checkUserPayProtocol(arguments)
            break
        case 'submitUserPayProtocol':
            result = submitUserPayProtocol(arguments)
            break
        case 'submitIdentity':
            result = submitIdentity(arguments)
            break
        case 'submitApplyStep':
            result = submitApplyStep(arguments)
            break
        default:
            result.put('结果', '没有执行方法')
            break
    }
    return result
}

// 登录app
static def loginApp(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userMobile = args.containsKey('userMobile') ? args.getString('userMobile') : ''
    String verifyCode = args.containsKey('verifyCode') ? args.getString('verifyCode') : ''
    String deviceId = args.containsKey('deviceId') ? args.getString('deviceId') : ''
    return loginApp(userMobile, verifyCode, deviceId)
}

static def loginApp(String userMobile, String verifyCode, String deviceId) {
    def params = ['deviceId': deviceId, 'account': userMobile, 'verifyCode': verifyCode]
    def token = '';
    try {
        def response = RestClient.doPostWithBody(frontUrl, loginAppPath, params, null)
        if (response == null) {
            logger.warn("user login App no response")
            return token;
        }
        logger.info("user login App response: {}", response.body())
        if (response.isOk()) {
            token = JSON.parseObject(response.body()).getJSONObject('responseObject').getString('token')
            String appTokenKey = APP_TOKEN_KEY + userMobile
            // save token key
            StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class)
            stringRedisTemplate.opsForValue().set(appTokenKey, token)
        }
    } catch (Exception ex) {
        logger.error("user login App failed,", ex)
    }
    //
    return token
}

// 获取app token
static def getAppToken(String arguments) {
    JSONObject args = JSON.parseObject(arguments);
    String userMobile = args.containsKey('userMobile') ? args.getString('userMobile') : ''
    String verifyCode = args.containsKey('verifyCode') ? args.getString('verifyCode') : ''
    String deviceId = args.containsKey('deviceId') ? args.getString('deviceId') : ''
    String appToken = ''
    if (!StrUtil.isEmptyIfStr(verifyCode)) {
        appToken = loginApp(userMobile, verifyCode, deviceId)
    }
    if (StrUtil.isEmptyIfStr(appToken)) {
        String appTokenKey = APP_TOKEN_KEY + userMobile;
        StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class)
        appToken = stringRedisTemplate.opsForValue().get(appTokenKey)
    }
    return appToken
}

// 发送登录验证码
static def sendLoginSms(String arguments) {
    JSONObject args = JSON.parseObject(arguments);
    String userMobile = args.containsKey('userMobile') ? args.getString('userMobile') : ''
    int randomInt = RandomUtil.getSecureRandom().nextInt(10)
    String checkKey = DigestUtil.md5Hex(('verify_code_check' + userMobile + randomInt).getBytes())
    def params = ['mobile': userMobile, 'random': String.valueOf(randomInt), 'requestKey': checkKey]
    def response = RestClient.doPostWithBody(frontUrl, getAppVerifyCodeCheckPath, params, null)
    logger.info("send login sms response: {}", response)
}

// 通知用户
static def notifyUser(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('userId') ? args.getString('userId') : ''
    String content = args.containsKey('content') ? args.getString('content') : ''
    logger.info("notify user: {}, content: {}", userId, content)
}

// 预申请
static def preApply(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    // post params body
    def params = ['mobile': mobile, 'showLoanList': 'DDG']
    // post headers
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, preApplyPath + token, params, requestAuth)
        if (response == null) {
            logger.warn("preApply no response")
            return null
        }
        logger.info("preApply response: {}", response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error("preApply error,", ex)
    }
    return null
}

// 加载用户信息
static def loadIdentity(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    // post params body
    def params = ['appId': appId]
    // post headers
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, loadIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error("loadIdentity no response")
            return null
        }
        logger.info('loadIdentity response: {}', response)
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error("loadIdentity error,", ex)
    }
    return null
}

// 识别身份证
static def doIdCardOcr(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String url = args.containsKey('url') ? args.getString('url') : ''
    String fileType = args.containsKey('fileType') ? args.getString('fileType') : ''
    // file dest
    File file = File.createTempFile('idPhoto', '.ycImage')
    // download file
    HttpUtil.downloadFile(url, file)
    // read file to bytes
    byte[] fileContent = FileUtil.readBytes(file)
    // base64 encoding
    String encodedString = Base64.getEncoder().encodeToString(fileContent)
    // post params body
    def params = ['fileType': fileType, 'imageData': 'data:image/jpeg;base64,' + encodedString]
    // post header
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, doIdCardOcrPath + token, params, requestAuth)
        if (response == null) {
            logger.error("doIdCardOcr error")
            return null
        }
        logger.info("doIdCardOcr response: {}", response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error("doIdCardOcr error,", ex)
    }
    return null
}

// check bank card limit
static def checkBankCardLimit(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String name = args.containsKey('name') ? args.getString('name') : ''
    String idNo = args.containsKey('idNo') ? args.getString('idNo') : ''
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    Integer amount = args.containsKey('amount') ? args.getInteger('amount') : null
    // post params body
    def params = ['name': name, 'idNo': idNo, 'bankCard': bankCard, 'loanAmt': amount]
    // post headers
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, checkBankCardLimitPath, params, requestAuth)
        if (response == null) {
            logger.error("checkBankCardLimit no response")
            return null
        }
        logger.info("checkBankCardLimit response:{}", response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error("checkBankCardLimit error,", ex)
    }
    return null
}

// check 老客户信息
static def checkOldIdentity(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String name = args.containsKey('name') ? args.getString('name') : ''
    String idNo = args.containsKey('idNo') ? args.getString('idNo') : ''
    def params = ['name': name, 'idNo': idNo]
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, checkOldIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error("checkIdentity no response")
            return null
        }
        logger.info("checkIdentity response:{}", response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error("checkIdentity error,", ex)
    }
    return null
}

// check 用户支付协议
static def checkUserPayProtocol(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String name = args.containsKey('name') ? args.getString('name') : ''
    String idNo = args.containsKey('idNo') ? args.getString('idNo') : ''
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    String bankMobile = args.containsKey('bankMobile') ? args.getString('bankMobile') : ''
    String bankCode = args.containsKey('bankCode') ? args.getString('bankCode') : ''
    // post params body
    def params = ['appId': appId, 'name': name, 'idNo': idNo, 'bankCode': bankCode, 'accountId': bankCard, 'rsvPhone': bankMobile, 'type': '1', 'pageUrl': 'https://www.baidu.com']
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, checkProtocolPath + token, params, requestAuth)
        if (response == null) {
            logger.error('checkUserPayProtocol no response')
            return null
        }
        logger.info('checkUserPayProtocol response: {}' + response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('checkUserPayProtocol error,', ex)
    }
    return null
}

// 提交用户支付协议
static def submitUserPayProtocol(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String bankCode = args.containsKey('bankCode') ? args.getString('bankCode') : ''
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    String bankMobile = args.containsKey('bankMobile') ? args.getString('bankMobile') : ''
    String makeProtocolKey = args.containsKey('makeProtocolKey') ? args.getString('makeProtocolKey') : ''
    String smsVerifyCode = args.containsKey('smsVerifyCode') ? args.getString('smsVerifyCode') : ''
    // post params body
    def params = ['appId': appId, 'cardCode': bankCode, 'reservedMobile': bankMobile, 'smsStr': smsVerifyCode, 'makeProtocolKey': makeProtocolKey, 'cardNo': bankCard, 'type': '1']
    // post headers
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, submitProtocolPath + token, params, requestAuth)
        if (response == null) {
            logger.error('submitUserPayProtocol no response')
            return null
        }
        logger.info('submitUserPayProtocol response: {}', response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('submitUserPayProtocol error,', ex)
    }
    return null
}

// 提交用户信息
static def submitIdentity(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String name = args.containsKey('name') ? args.getString('name') : ''
    String idNo = args.containsKey('idNo') ? args.getString('idNo') : ''
    String idValid = args.containsKey('idValid') ? args.getString('idValid') : ''
    String bankCode = args.containsKey('bankCode') ? args.getString('bankCode') : ''
    String storeCode = args.containsKey('storeCode') ? args.getString('storeCode') : ''
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    String bankMobile = args.containsKey('bankMobile') ? args.getString('bankMobile') : ''
    // post params body
    def params = ['C_APP_ID': appId, 'name': name, 'C_ID_VALID': idValid, 'idNo': idNo, 'bankCode': bankCode, 'storeCode': storeCode, 'accountId': bankCard, 'rsvPhone': bankMobile]
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, submitIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error('submitIdentity no response')
            return null
        }
        logger.info('submitIdentity response: {}', response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('submitIdentity error,', ex)
    }
    return null
}

// 提交申请
static def submitApplyStep(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String token = args.containsKey('token') ? args.getString('token') : ''
    JSONObject info = args.containsKey('info') ? args.getJSONObject('info') : null
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, submitApplyStepPath + token, info, requestAuth)
        if (response == null) {
            logger.error('submitApplyStep no response')
            return null
        }
        logger.info('submitApplyStep response: {}', response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('submitApplyStep error,', ex)
    }
    return null
}

static def wrapHeadersWithToken(String token) {
    def defaultHeaders = ['platform': 'wechat']
    if (!StrUtil.isEmptyIfStr(token)) {
        defaultHeaders.put('appToken', token)
    }
    return defaultHeaders
}