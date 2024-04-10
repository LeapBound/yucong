package scripts.loan

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.extra.spring.SpringUtil
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.CamundaService
import com.github.leapbound.yc.action.func.groovy.RequestAuth
import com.github.leapbound.yc.action.func.groovy.RestClient
import com.github.leapbound.yc.camunda.model.vo.TaskReturn
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate

import java.util.concurrent.TimeUnit

/**
 *
 * @author yamath
 * @since 2024/3/26 9:42
 */
@Field static String frontUrl = 'https://beta.geexfinance.com'
@Field static String loginAppPath = '/front-api/geex_capp/v1/user/loginApp'
@Field static String getAppVerifyCodeCheckPath = '/front-api/geex_capp/v1/sms/getAppVerifyCodeCheck'
@Field static String getCommonVerifyCodePath = '/front-api/geex_capp/v1/newOrder/inner/alpha/common/verification/code'
@Field static String preApplyPath = '/front-api/geex_capp/v1/order/preApply/'
@Field static String loadIdentityPath = '/front-api/geex_capp/v1/user/loadIdentity/'
@Field static String doIdCardOcrPath = '/front-api/geex_capp/v1/media/doIdCardOcr/'
@Field static String checkBankCardLimitPath = '/front-api/geex_capp/v1/order/checkBankCardLimit'
@Field static String checkOldIdentityPath = '/front-api/geex_capp/v1/user/checkOldIdentity/'
@Field static String checkProtocolPath = '/front-api/geex_capp/v1/user/checkProtocol/'
@Field static String submitProtocolPath = '/front-api/geex_capp/v1/user/submitProtocol/'
@Field static String submitIdentityPath = '/front-api/geex_capp/v1/user/submitIdentity/'
@Field static String submitApplyStepPath = '/front-api/geex_capp/v1/apply/submitApplyStep/'
@Field static String hubUrl = 'http://192.168.8.232:8088'
@Field static String noticeHubPath = '/yc-hub/api/conversation/notice'
@Field static String APP_TOKEN_KEY = 'yc.a.s.app.token.'
@Field static Logger logger = LoggerFactory.getLogger('scripts.loan.Loan');

execLoanMethod(method, arguments)

static def execLoanMethod(String method, String arguments) {
    JSONObject result = new JSONObject()
    // check arguments
    if (arguments == null || arguments.isEmpty()) {
        result.put('错误', '没有提供必要的信息')
        return result
    }
    def externalUrl = getExternalUrl(arguments)
    if (!StrUtil.isEmptyIfStr(externalUrl)) {
        frontUrl = externalUrl
    }
    switch (method) {
        case 'start_loan_process':
            result = startLoanProcess(arguments)
            break
        case 'delete_loan_process':
            result = deleteLoanProcess(arguments)
            break
        case 'bind_mobile':
            result = bindMobile(arguments)
            break
        case 'send_code': // service task called by java delegate
            result = sendCode(arguments)
            break
        case 'verify_mobile_code':
            result = verifyMobileCode(arguments)
            break
        case 'config_by_bd_mobile':
            result = configByBdMobile(arguments)
            break
        case 'product_info':
            result = productInfo(method, arguments)
            break
        case 'term_config':
            result = termConfig(arguments)
            break
        case 'loan_term':
            result = loanTerm(method, arguments)
            break
        case 'load_client_identity': // service task called by java delegate
            loadClientIdentity(arguments)
            break
        case 'id_photo_front':
            result = doIdCardOcr(method, arguments)
            break
        case 'id_photo_back':
            result = doIdCardOcr(method, arguments)
            break
        case 'bank_card':
            result = bankCard(method, arguments)
            break
        case 'check_bank_card': // service task called by java delegate
            result = checkBankCard(arguments)
            break
        case 'submit_pay_protocol':
            result = submitPayProtocol(method, arguments)
            break
        case 'second_step': // service task called by java delegate
            result = secondStep(arguments)
            break
        case 'third_step':
            result = thirdStep(method, arguments)
            break
        case 'forth_step':
            result = forthStep(method, arguments)
            break
        case 'submit_audit': // service task called by java delegate
            result = submitAudit(arguments)
            break
        default:
            result.put('结果', '没有执行方法')
            break
    }
    return result
}

static def getExternalUrl(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String externalHost = args.containsKey('externalHost') ? args.getString('externalHost') : ''
    return externalHost
}

//
static def startLoanProcess(String arguments) {
    JSONObject result = new JSONObject()
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    // process key = 'Process_chatin'
    String processInstanceId = CamundaService.startProcess('Process_chatin', userId)
    TaskReturn taskReturn = CamundaService.queryCurrentTask(userId); // next task
    def preContent = '好的，请提供您的手机号'
    CamundaService.setTaskVariableLocal(['preContent': preContent], processInstanceId, taskReturn.getTaskId())
    result.put('functionContent', preContent)
    return result
}

static def deleteLoanProcess(String arguments) {
    JSONObject result = new JSONObject()
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    try {
        CamundaService.deleteProcess(userId)
        result.put('functionContent', '好的，您的贷款申请已取消')
    } catch (Exception ex) {
        logger.error('deleteLoanProcess error, ', ex)
        result.put('functionContent', '后台错误，联系管理员')
    }
    return result
}

// 绑定手机号
static def bindMobile(String arguments) {
    JSONObject result = new JSONObject()
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
    if (taskReturn == null) {
        logger.error('bindMobile no current task, businessKey: {}', userId)
        result.put('functionContent', '获取流程失败，请联系管理员')
    } else {
        def taskId = taskReturn.getTaskId()
        def inputForm = CamundaService.fillCurrentForm(taskReturn.getCurrentInputForm(), args)
        CamundaService.completeTask(taskId, inputForm)
    }
    return result
}

static def sendCode(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    sendLoginSms(mobile)
    notifyUser(mobile, '验证码已经发送，请在收到验证码后发送给我')
    // set send_code output
    return new JSONObject() {
        {
            put('z_sendUserMobileVerifyCode', true)
        }
    }
}

// 登录app
static def loginApp(String userMobile, String verifyCode, String deviceId) {
    def params = ['deviceId': deviceId, 'account': userMobile, 'veriCode': verifyCode]
    def token = '';
    try {
        def response = RestClient.doPostWithBody(frontUrl, loginAppPath, params, null)
        if (response == null) {
            logger.error('user login App no response')
            return token;
        }
        logger.info('user login App response: {}', response.body())
        if (response.isOk()) {
            JSONObject result = JSON.parseObject(response.body())
            if (result.getBooleanValue('result')) {
                token = result.getJSONObject('responseObject').getString('token')
                String appTokenKey = APP_TOKEN_KEY + userMobile
                // save token key
                StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class)
                stringRedisTemplate.opsForValue().set(appTokenKey, token, 3600, TimeUnit.SECONDS)
            } else {
                logger.error('user login App failed, result: {}', result.getString('errMsg'))
            }
        }
    } catch (Exception ex) {
        logger.error('user login App failed,', ex)
    }
    //
    return token
}

// get app token
static def getAppToken(String mobile, String verifyCode, String deviceId) {
    String appToken = ''
    if (!StrUtil.isEmptyIfStr(verifyCode)) {
        appToken = loginApp(mobile, verifyCode, deviceId)
    }
    if (StrUtil.isEmptyIfStr(appToken)) {
        String appTokenKey = APP_TOKEN_KEY + mobile;
        StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class)
        appToken = stringRedisTemplate.opsForValue().get(appTokenKey)
    }
    if (StrUtil.isEmptyIfStr(appToken) && StrUtil.isEmptyIfStr(verifyCode)) {
        String commonVerifyCode = getCommonVerifyCode()
        if (StrUtil.isEmptyIfStr(commonVerifyCode)) {
            return appToken
        }
        appToken = getAppToken(mobile, commonVerifyCode, deviceId)
    }
    return appToken
}

static def getCommonVerifyCode() {
    def response = RestClient.doGet(frontUrl, getCommonVerifyCodePath, null, null)
    if (response == null) {
        logger.error("getCommonVerifyCode no response")
        return null
    }
    return JSON.parseObject(response.body()).getString('responseObject')
}

// 发送登录验证码
static def sendLoginSms(String userMobile) {

    JSONObject result = new JSONObject()
    int randomInt = RandomUtil.getSecureRandom().nextInt(10)
    String checkKey = DigestUtil.md5Hex(('verify_code_check' + userMobile + randomInt).getBytes())
    def params = ['mobile': userMobile, 'random': String.valueOf(randomInt), 'requestKey': checkKey]
    def response = RestClient.doPostWithBody(frontUrl, getAppVerifyCodeCheckPath, params, null)
    if (response == null) {
        logger.error('send login sms no response')
    }
    logger.info('send login sms response: {}', response.body()) // 回调hub通知接口
}

// 通知用户
static def notifyUser(String userId, String content) {
    logger.info('notify user: {}, content: {}', userId, content)
    def result = ['accountId': userId, 'content': content]
    noticeHub(result)
}

static def verifyMobileCode(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String verifyCode = args.containsKey('smsCode') ? args.getString('smsCode') : ''
    String deviceId = args.containsKey('deviceId') ? args.getString('deviceId') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('verifyMobileCode no current task, businessKey: {}', userId)
            result.put('functionContent', '获取当前流程失败，请联系管理员')
        } else {
            def taskId = taskReturn.getTaskId()
            def processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            //
            String appToken = getAppToken(mobile, verifyCode, deviceId)
            if (!StrUtil.isEmptyIfStr(appToken)) {
                args.put('z_userMobileVerify', true)
                def inputForm = CamundaService.fillCurrentForm(taskReturn.getCurrentInputForm(), args)
                CamundaService.completeTask(taskId, inputForm)
            }
        }
    } catch (Exception ex) {
        logger.error('verifyMobileCode error, ', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result // 通用话术
}

static def configByBdMobile(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String bdMobile = args.containsKey('bdMobile') ? args.getString('bdMobile') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('configByBdMobile no current task, businessKey: {}', userId)
            result.put('functionContent', '获取当前流程失败，请联系管理员')
        } else {
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appToken = getAppToken(mobile, null, null)
            JSONObject storeConfig = preApply(appToken, bdMobile)
            String storeCode = storeConfig.containsKey('storeCode') ? storeConfig.getString('storeCode') : ''
            JSONObject config = storeConfig.containsKey('config') ? storeConfig.getJSONObject('config') : null
            def configForm = ['bdMobile': bdMobile, 'storeCode': storeCode, 'loanConfig': config]
            CamundaService.completeTask(taskId, configForm)
        }
    } catch (Exception ex) {
        logger.error('configByBdMobile error, ', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result
}

static def preApply(String token, String mobile) {
    // post params body
    def params = ['mobile': mobile, 'showLoanList': 'DDG']
    // post headers
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, preApplyPath + token, params, requestAuth)
        if (response == null) {
            logger.warn('preApply no response')
            return null
        }
        logger.info('preApply response: {}', response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('preApply error,', ex)
    }
    return null
}

static def productInfo(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String productName = args.containsKey('productName') ? args.getString('productName') : ''
    Integer applyAmount = args.containsKey('productAmount') ? args.getInteger('productAmount') : null
    if (StrUtil.isEmptyIfStr(productName) || null == applyAmount) {
        result.put('functionContent', '获取产品失败，联系管理员')
        return result
    }
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('productInfo no current task, businessKey: {}', userId)
            result.put('functionContent', '获取当前流程失败，请联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                result.put('functionContent', '当前流程操作失败，请联系管理员')
                return result
            }
            String taskId = taskReturn.getTaskId()
            def loanInfoForm = ['applyAmount': applyAmount, 'productName': productName]
            CamundaService.completeTask(taskId, loanInfoForm)
        }
    } catch (Exception ex) {
        logger.error('productInfo error,', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result
}

static def termConfig(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject loanConfig = args.containsKey('loanConfig') ? args.getJSONObject('loanConfig') : null
    JSONArray stages = (loanConfig != null && loanConfig.containsKey('StageCount')) ? loanConfig.getJSONArray('StageCount') : null
    if (stages == null || stages.isEmpty()) {
        logger.error('no StageCount found in loanConfig')
        return null
    }
    Set<String> terms = new HashSet<>()
    for (JSONObject stage : stages) {
        String value = stage.getString('value')
        if ('请选择' != value) {
            terms.add(value)
        }
    }
    return new JSONObject() {
        {
            put('termConfig', terms)
        }
    }
}

static def loanTerm(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String loanTerm = args.containsKey('loanTerm') ? args.getString('loanTerm') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('loanTerm no current task, businessKey: {}', userId)
            result.put('functionContent', '获取当前流程失败，请联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                result.put('functionContent', '当前流程操作失败，请联系管理员')
                return result
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            def loanConfig = processVariable.containsKey('loanConfig') ? processVariable.getJSONObject('loanConfig') : null
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            Integer amount = processVariable.containsKey('applyAmount') ? processVariable.getInteger('applyAmount') : 0
            String productName = processVariable.containsKey('productName') ? processVariable.getString('productName') : ''

            String appToken = getAppToken(mobile, null, null)

            def stages = loanConfig.containsKey('StageCount') ? loanConfig.getJSONArray('StageCount') : null
            String loanProductId = ''
            for (JSONObject stage : stages) {
                String value = stage.getString('value')
                if (loanTerm == value) {
                    loanProductId = stage.getString('key')
                }
            }
            JSONObject firstSubmitObj = new JSONObject() {
                {
                    put('C_STEP_ID', 'NYB01_01');
                    put('C_FORM_ID', 'NYB01');
                    put('C_ITEM_NAME', productName);
                    put('N_AMT_APPLIED', amount);
                    put('N_GEEX_LOAN_PDT_ID', loanProductId);
                }
            }
            def firstSubmit = submitApplyStep(appToken, firstSubmitObj)
            String appId = firstSubmit.containsKey('C_APP_ID') ? firstSubmit.getString('C_APP_ID') : ''
            def appIdForm = ['appId': appId, 'loanTerm': loanTerm]
            CamundaService.completeTask(taskId, appIdForm)
        }
    } catch (Exception ex) {
        logger.error('loanTerm error, ', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result
}

// 加载用户信息
static def loadClientIdentity(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    //
    String appToken = getAppToken(mobile, null, null)
    return loadIdentity(appId, appToken);
}

static def loadIdentity(String appId, String token) {
    // post params body
    def params = ['appId': appId]
    // post headers
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, loadIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error('loadIdentity no response')
            return null
        }
        logger.info('loadIdentity response: {}', response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('loadIdentity error,', ex)
    }
    return null
}

static def doIdCardOcr(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String url = args.containsKey('idPhotoUrl') ? args.getString('idPhotoUrl') : ''
    String fileType = args.containsKey('idPhotoType') ? args.getString('idPhotoType') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('doIdCardOcr no current task, businessKey: {}', userId)
            result.put('functionContent', '获取当前流程失败，请联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                result.put('functionContent', '当前流程操作失败，请联系管理员')
                return result
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appToken = getAppToken(mobile, null, null)
            JSONObject ocrResult = doIdCardOcr(url, fileType, appToken)
            def inputForm = new HashMap() {
                {
                    put('ocr' + fileType, ocrResult)
                }
            }
            CamundaService.completeTask(taskId, inputForm)
        }
    } catch (Exception ex) {
        logger.error('doIdCardOcr error', ex)
        result.put('错误', '系统错误，请联系管理员')
    }
    return result
}

// 识别身份证
static def doIdCardOcr(String url, String fileType, String token) {
    // file dest
    File file = File.createTempFile('idPhoto', '.ycImage')
    // download file
    HttpUtil.downloadFile(url, file)
    // read file to bytes
    byte[] fileContent = FileUtil.readBytes(file)
    // base64 encoding
    String encodedString = Base64.getEncoder().encodeToString(fileContent)
    // post params body
    def params = ['fileType': fileType, 'imgData': 'data:image/jpeg;base64,' + encodedString]
    // post header
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithForm(frontUrl, doIdCardOcrPath + token, params, requestAuth)
        if (response == null) {
            logger.error('doIdCardOcr error')
            return null
        }
        logger.info('doIdCardOcr response: {}', response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('doIdCardOcr error,', ex)
    }
    return null
}

static def bankCard(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    String bankMobile = args.containsKey('bankMobile') ? args.getString('bankMobile') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId);
        if (taskReturn == null) {
            logger.error('bankCard no current task, businessKey: {}', userId)
            result.put('functionContent', '无法获取当前流程，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                result.put('functionContent', '当前流程失败，请联系管理员')
                return result
            }
            String taskId = taskReturn.getTaskId()
            def inputForm = ['bankCard': bankCard, 'bankMobile': bankMobile]
            CamundaService.completeTask(taskId, inputForm)
        }
    } catch (Exception ex) {
        logger.error('bankCard error,', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result
}

static def checkBankCard(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    Integer amount = args.containsKey('applyAmount') ? args.getInteger('applyAmount') : null
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    String bankMobile = args.containsKey('bankMobile') ? args.getString('bankMobile') : ''
    String bankCode = args.containsKey('bankCode') ? args.getString('bankCode') : ''
    JSONObject ocrFront = args.containsKey('ocridnoFront') ? args.getJSONObject('ocridnoFront') : null
    JSONObject ocrFrontDetail = (ocrFront != null && ocrFront.containsKey('ocrDetail')) ? ocrFront.getJSONObject('ocrDetail') : null
    String name = (ocrFrontDetail != null && ocrFrontDetail.containsKey('name')) ? ocrFrontDetail.getString('name') : ''
    String idNo = (ocrFrontDetail != null && ocrFrontDetail.containsKey('idCardNumber')) ? ocrFrontDetail.getString('idCardNumber') : ''
    JSONObject ocrBack = args.containsKey('ocridnoBack') ? args.getJSONObject('ocridnoBack') : null
    JSONObject ocrBackDetail = (ocrBack != null && ocrBack.containsKey('ocrDetail')) ? ocrBack.getJSONObject('ocrDetail') : null
    String idValid = (ocrBackDetail != null && ocrBackDetail.containsKey('validDate')) ? ocrBackDetail.getString('validDate') : ''

    String appToken = getAppToken(mobile, null, null)
    checkBankCardLimit(appToken, name, idNo, bankCard, amount)

    checkOldIdentity(appToken, name, idNo)

    JSONObject checkProtocolResult = checkPayProtocol(appToken, appId, name, idNo, bankCard, bankMobile, bankCode)
    String protocolKey = checkProtocolResult != null && checkProtocolResult.containsKey('makeProtocolKey') ? checkProtocolResult.getString('makeProtocolKey') : ''
    return new JSONObject() {
        {
            put('payProtocolKey', protocolKey)
        }
    }
}

// check bank card limit
static def checkBankCardLimit(String token, String name, String idNo, String bankCard, Integer amount) {
    // post params body
    def params = ['name': name, 'idNo': idNo, 'bankCard': bankCard, 'loanAmt': amount]
    // post headers
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, checkBankCardLimitPath, params, requestAuth)
        if (response == null) {
            logger.error('checkBankCardLimit no response')
            return null
        }
        logger.info('checkBankCardLimit response:{}', response.body())
        if (response.isOk()) {
            String result = JSON.parseObject(response.body()).getString('responseObject')
            return new JSONObject() {
                {
                    put('result', result)
                }
            }
        }
    } catch (Exception ex) {
        logger.error('checkBankCardLimit error,', ex)
    }
    return null
}

// check 老客户信息
static def checkOldIdentity(String token, String name, String idNo) {
    def params = ['name': name, 'idNo': idNo]
    def headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        def response = RestClient.doPostWithBody(frontUrl, checkOldIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error('checkIdentity no response')
            return null
        }
        logger.info('checkIdentity response:{}', response.body())
        if (response.isOk()) {
            return JSON.parseObject(response.body()).getJSONObject('responseObject')
        }
    } catch (Exception ex) {
        logger.error('checkIdentity error,', ex)
    }
    return null
}

// check 用户支付协议
static def checkPayProtocol(String token, String appId, String name, String idNo, String bankCard, String bankMobile, String bankCode) {
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
static def submitPayProtocol(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String verifyCode = args.containsKey('payProtocolVerifyCode') ? args.getString('payProtocolVerifyCode') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('submitUserPayProtocol no current task, businessKey: {}', userId)
            result.put('functionContent', '提交支付协议失败， 联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                result.put('functionContent', '当前流程失败，请联系管理员')
                return result
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appId = processVariable.containsKey('appId') ? processVariable.getString('appId') : ''
            String payProtocolKey = processVariable.containsKey('payProtocolKey') ? processVariable.getString('payProtocolKey') : ''
            String bankCard = processVariable.containsKey('bankCard') ? processVariable.getString('bankCard') : ''
            String bankMobile = processVariable.containsKey('bankMobile') ? processVariable.getString('bankMobile') : ''
            String bankCode = processVariable.containsKey('bankCode') ? processVariable.getString('bankCode') : ''
            String storeCode = processVariable.containsKey('storeCode') ? processVariable.getString('storeCode') : ''
            def ocrFront = processVariable.containsKey('ocridnoFront') ? processVariable.getJSONObject('ocridnoFront') : null
            def ocrFrontDetail = ocrFront.containsKey('ocrDetail') ? ocrFront.getJSONObject('ocrDetail') : null
            String name = ocrFrontDetail.containsKey('name') ? ocrFrontDetail.getString('name') : ''
            String idNo = ocrFrontDetail.containsKey('idCardNumber') ? ocrFrontDetail.getString('idCardNumber') : ''
            def ocrBack = processVariable.containsKey('ocridnoBack') ? processVariable.getJSONObject('ocridnoBack') : null
            def ocrBackDetail = ocrBack.containsKey('ocrDetail') ? ocrBack.getJSONObject('ocrDetail') : null
            String idValid = ocrBackDetail.containsKey('validDate') ? ocrBackDetail.getString('validDate') : ''
            String appToken = getAppToken(mobile, null, null)
            String submitResult = submitPayProtocol(appId, bankCode, bankMobile, verifyCode, payProtocolKey, bankCard, appToken)
            logger.info('submitPayProtocol result: {}', submitResult)
            def submitIdentityResult = submitIdentity(appId, name, idNo, idValid, bankCode, storeCode, bankCard, bankMobile, appToken)
            logger.info('submitIdentity result: {}', submitIdentityResult)
            CamundaService.completeTask(taskId, [:])
        }
    } catch (Exception ex) {
        logger.error('processPayProtocol error, ', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result
}

static def submitPayProtocol(String appId, String bankCode, String bankMobile, String verifyCode, String payProtocolKey, String bankCard, String token) {

    // post params body
    def params = ['appId': appId, 'cardCode': bankCode, 'reservedMobile': bankMobile, 'smsStr': verifyCode, 'makeProtocolKey': payProtocolKey, 'cardNo': bankCard, 'type': '1']
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
            return JSON.parseObject(response.body()).getString('responseObject')
        }
    } catch (Exception ex) {
        logger.error('submitUserPayProtocol error,', ex)
    }
    return null
}

static def submitIdentity(String appId, String name, String idNo, String idValid, String bankCode,
                          String storeCode, String bankCard, String bankMobile, String token) {
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

static def secondStep(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    String appToken = getAppToken(mobile, null, null)
    JSONObject info = new JSONObject() {
        {
            put('C_APP_ID', appId)
            put('C_STEP_ID', 'NYB01_02')
            put('C_DEVICE_TYPE', 'bot')
            put('C_FORM_ID', 'NYB01')
        }
    }
    return submitApplyStep(appToken, info)
}

// 提交申请
static def submitApplyStep(String token, JSONObject info) {
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

static def thirdStep(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String maritalStatus = args.containsKey('maritalStatus') ? args.getString('maritalStatus') : ''
    try {


        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('thirdStep no current task, businessKey: {}', userId)
            result.put('functionContent', '当前流程失败，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                result.put('functionContent', '当前流程失败，请联系管理员')
                return result
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appId = processVariable.containsKey('appId') ? processVariable.getString('appId') : ''
            JSONObject stepInputForm = new JSONObject() {
                {
                    put('C_APP_ID', appId)
                    put('C_MARITAL', maritalStatus)
                    put('C_STEP_ID', 'NYB01_03')
                }
            }
            String appToken = getAppToken(mobile, null, null)
            submitApplyStep(appToken, stepInputForm)
            CamundaService.completeTask(taskId, [:])
        }
    } catch (Exception ex) {
        logger.error('thirdStep error,', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result
}

static def forthStep(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String userId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String companyName = args.containsKey('companyName') ? args.getString('companyName') : ''
    String mailAddr = args.containsKey('mailAddr') ? args.getString('mailAddr') : ''
    try {


        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('thirdStep no current task, businessKey: {}', userId)
            result.put('functionContent', '当前流程失败，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                result.put('functionContent', '当前流程失败，请联系管理员')
                return result
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appId = processVariable.containsKey('appId') ? processVariable.getString('appId') : ''
            JSONObject stepInputForm = new JSONObject() {
                {
                    put('C_APP_ID', appId)
                    put('C_COMP_NAME', companyName)
                    put('C_STEP_ID', 'NYB01_04')
                    put('C_MAIL_ADDR', mailAddr)
                }
            }
            String appToken = getAppToken(mobile, null, null)
            submitApplyStep(appToken, stepInputForm)
            CamundaService.completeTask(taskId, [:])
        }
    } catch (Exception ex) {
        logger.error('forthStep error, ', ex)
        result.put('functionContent', '系统错误，请联系管理员')
    }
    return result
}

static def submitAudit(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    String appToken = getAppToken(mobile, null, null)
    JSONObject info = new JSONObject() {
        {
            put('C_APP_ID', appId)
            put('C_STEP_ID', 'PREVIEW')
        }
    }
    return submitApplyStep(appToken, info)
}

static def wrapHeadersWithToken(String token) {
    def defaultHeaders = ['platform': 'wechat']
    if (!StrUtil.isEmptyIfStr(token)) {
        defaultHeaders.put('appToken', token)
    }
    return defaultHeaders
}

static def checkTaskPosition(String method, String taskName) {
    return method == taskName
}

static def noticeHub(Map<String, Object> result) {
    def response = RestClient.doPostWithBody(hubUrl, noticeHubPath, result, null)
}