package scripts.loan

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.extra.spring.SpringUtil
import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.*
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
@Field static String dingdanUrl = ''
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
@Field static String submitApplyStepPath = '/front2-provider/geex_capp/v2/submit/step/submitApplyStep/'
@Field static String getSupportBankListPath = '/front2-provider/geex_capp/v1/payment/getSupportBankList'
@Field static String faceDetectPath = '/front2-provider/geex_capp/v1/order/getStoreConfigs'
@Field static String webankH5Path = '/front2-provider/geex_capp/v1/face/getH5Login'
@Field static String validateH5FacePath = '/front2-provider/geex_capp/v1/face/validateH5Face'
@Field static String gonggongUrl = ''
@Field static String noticeHubPath = '/geex-smart-robot/yc-hub/api/conversation/notice'
@Field static String APP_TOKEN_KEY = 'yc.a.s.app.token.'
@Field static Map<String, String> maritalStatusMap = ['未婚': '01', '已婚': '02', '离异': '03', '其他': '04']
@Field static Map<String, String> relationMap = ['配偶': '01', '父母': '03', '子女': '04', '亲属': '10']

@Field static Logger logger = LoggerFactory.getLogger('scripts.loan.Loan');

execLoanMethod(method, arguments)

static def execLoanMethod(String method, String arguments) {
    JSONObject result = new JSONObject()
    // check arguments
    if (StrUtil.isBlankIfStr(arguments)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供必要的信息')
    }
    // get external args
    Map<String, String> externalArgs = CommonMethod.getExternalArgs();
    gonggongUrl = externalArgs.get('gonggongUrl')
    dingdanUrl = externalArgs.get('dingdanUrl')
    //
    switch (method) {
        case 'start_loan_process':
            result = startLoanProcess(arguments)
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
        case 'loan_config':
            result = loanConfig(arguments)
            break
        case 'loan_term':
            result = loanTerm(method, arguments)
            break
        case 'load_client_identity': // service task called by java delegate
            result = loadClientIdentity(arguments)
            break
        case 'id_photo_front':
            result = doIdCardOcr(method, arguments)
            break
        case 'id_photo_back':
            result = doIdCardOcr(method, arguments)
            break
        case 'bank_code_config':
            result = bankCodeConfig(arguments)
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
        case 'contract_preview_notice': // service task called by java delegate
            result = contractPreviewNotice(arguments)
            break
        case 'contract_preview':
            result = contractPreview(method, arguments)
            break
        case 'second_step': // service task called by java delegate
            result = secondStep(arguments)
            break
        case 'marital_status':
            result = maritalStatus(method, arguments)
            break
        case 'relation_info':
            result = relationInfo(method, arguments)
            break
        case 'third_step':
            result = thirdStep(method, arguments)
            break
        case 'forth_step':
            result = forthStep(method, arguments)
            break
        case 'face_detect': // service task called by java delegate
            result = faceDetect(arguments)
            break
        case 'face_verify':
            result = faceVerify(method, arguments)
            break
        case 'submit_audit': // service task called by java delegate
            result = submitAudit(arguments)
            break
        case 'notice_hub':
            noticeHub(arguments)
            break
        case 'delete_loan_process':
            result = deleteLoanProcess(arguments)
            break
        default:
            result = ResponseVo.makeFail(GeneralCodes.MISSING_EXEC_METHOD, '没有对应的执行方法')
            break
    }
    return result
}

//
static def startLoanProcess(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String externalId = args.containsKey('externalId') ? args.getString('externalId') : ''
    String botId = args.containsKey('botid') ? args.getString('botid') : ''
    Map<String, Object> startFormVariables = new HashMap() {
        {
            put('accountId', userId)
            put('botId', botId)
            put('externalId', externalId)
        }
    }
    // process key = 'Process_chatin'
    String processInstanceId = CamundaService.startProcess('Process_chatin', userId, startFormVariables)
    //
    logger.info('{},{}, start_loan_process', userId, externalId)
    return ResponseVo.makeSuccess(null)
}

// 绑定手机号
static def bindMobile(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
    if (taskReturn == null) {
        logger.error('bindMobile no current task, businessKey: {}', userId)
        return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[bind_mobile]获取流程失败，请联系管理员')
    } else {
        CamundaService.completeTaskByArgs(taskReturn, args)
        return ResponseVo.makeSuccess(null)
    }
}
// service task
static def sendCode(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    AppCommonResult appCommonResult = sendLoginSms(mobile)
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(false, appCommonResult.errMsg, data)
        return new JSONObject() {
            {
                put('s_taskResult', false)
                put('z_sendUserMobileVerifyCode', true)
                put('afterFunction', afterFunctionMap)
            }
        }
    }
    if (appCommonResult.responseObject != null) {
        result.putAll(JSONObject.parseObject(JSON.toJSONString(appCommonResult.responseObject)))
    }
    //
    Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
    result.put('z_sendUserMobileVerifyCode', true)
    result.put('afterFunction', afterFunctionMap)
    result.put('s_taskResult', appCommonResult.result)
    // set send_code output
    return result
}

static def verifyMobileCode(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String verifyCode = args.containsKey('smsCode') ? args.getString('smsCode') : ''
    String deviceId = args.containsKey('deviceId') ? args.getString('deviceId') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('verifyMobileCode no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[verify_mobile_code]获取当前流程失败，联系管理员')
        } else {
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            //
            String appToken = getAppToken(mobile, verifyCode, deviceId)
            if (!StrUtil.isEmptyIfStr(appToken)) {
                args.put('z_userMobileVerify', true)
                CamundaService.completeTaskByArgs(taskReturn, args)
                return ResponseVo.makeSuccess(null)
            }
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_COMPLETE_FAILED, '验证登录失败')
        }
    } catch (Exception ex) {
        logger.error('verifyMobileCode error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[verify_mobile_code]系统错误，联系管理员')
    }
}

static def configByBdMobile(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String bdMobile = args.containsKey('bdMobile') ? args.getString('bdMobile') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('configByBdMobile no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[config_bd_mobile]获取当前流程失败，联系管理员')
        } else {
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appToken = getAppToken(mobile, null, null)
            AppCommonResult appCommonResult = preApply(appToken, bdMobile)
            if (!appCommonResult.result) {
                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            JSONObject storeConfig = JSON.parseObject(JSON.toJSONString(appCommonResult.responseObject))
            String storeCode = storeConfig.containsKey('storeCode') ? storeConfig.getString('storeCode') : ''
            JSONObject config = storeConfig.containsKey('config') ? storeConfig.getJSONObject('config') : null
            Map<String, Object> configForm = new HashMap() {
                {
                    put('bdMobile', bdMobile)
                    put('storeCode', storeCode)
                    put('loanConfig', config)
                }
            }
            String taskId = taskReturn.getTaskId()
            CamundaService.completeTask(taskId, configForm)
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('configByBdMobile error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[config_bd_mobile]系统错误，请联系管理员')
    }
}

static def productInfo(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String productName = args.containsKey('productName') ? args.getString('productName') : ''
    Integer applyAmount = args.containsKey('productAmount') ? args.getInteger('productAmount') : null
    if (StrUtil.isEmptyIfStr(productName) || null == applyAmount) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '获取产品信息失败')
    }
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('productInfo no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[product_info]获取当前流程失败，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[product_info]当前流程操作失败，请联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            Map<String, Object> loanInfoForm = new HashMap() {
                {
                    put('applyAmount', applyAmount)
                    put('productName', productName)
                }
            }
            CamundaService.completeTask(taskId, loanInfoForm)
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('productInfo error,', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[product_info]系统错误，请联系管理员')
    }
}

static def loanConfig(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject loanConfig = args.containsKey('loanConfig') ? args.getJSONObject('loanConfig') : null
    JSONObject result = new JSONObject()
    // 产品
    JSONArray stages = (loanConfig != null && loanConfig.containsKey('StageCount')) ? loanConfig.getJSONArray('StageCount') : null
    if (stages != null && !stages.isEmpty()) {
        Set<String> terms = new HashSet<>()
        for (int i = 0; i < stages.size(); i++) {
            JSONObject stage = stages.getJSONObject(i)
            if (stage.containsKey('value')) {
                String value = stage.getString('value')
                if ('请选择' != value) {
                    terms.add(value)
                }
            }
        }
        result.put('termConfig', terms)
    } else {
        logger.warn('no StageCount found in loanConfig')
    }
    // 婚姻状态
    JSONArray married = (loanConfig != null && loanConfig.containsKey('Married')) ? loanConfig.getJSONArray('Married') : null
    if (married != null && !married.isEmpty()) {
        Set<String> maritalStatus = new HashSet<>()
        for (int i = 0; i < married.size(); i++) {
            JSONObject statusItem = married.getJSONObject(i)
            if (statusItem.containsKey('value')) {
                String value = statusItem.getString('value')
                if ('请选择' != value) {
                    maritalStatus.add(value)
                }
            }
        }
        result.put('maritalStatus', maritalStatus)
    } else {
        logger.warn('no Married found in loanConfig')
    }
    // relation
    JSONArray relationShip = (loanConfig != null && loanConfig.containsKey('Relationship')) ? loanConfig.getJSONArray('Relationship') : null
    if (relationShip != null && !relationShip.isEmpty()) {
        Set<String> relation = new HashSet<>()
        for (int i = 0; i < relationShip.size(); i++) {
            JSONObject relationItem = relationShip.getJSONObject(i)
            if (relationItem.containsKey('value')) {
                String value = relationItem.getString('value')
                if ('请选择' != value) {
                    relation.add(value)
                }
            }
        }
        result.put('relationShip', relation)
    } else {
        logger.warn('no Relationship found in loanConfig')
    }
    //
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())
    //
    Map<String, Object> afterFunctionMap = noticeMap(data)
    result.put('afterFunction', afterFunctionMap)
    result.put('s_taskResult', true)
    //
    return result
}

static def loanTerm(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String loanTerm = args.containsKey('loanTerm') ? args.getString('loanTerm') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('loanTerm no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[loan_term]获取当前流程失败，请联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[loan_term]当前流程操作失败，请联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            JSONObject loanConfig = processVariable.containsKey('loanConfig') ? processVariable.getJSONObject('loanConfig') : null
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            Integer amount = processVariable.containsKey('applyAmount') ? processVariable.getInteger('applyAmount') : 0
            String productName = processVariable.containsKey('productName') ? processVariable.getString('productName') : ''
            // token
            String appToken = getAppToken(mobile, null, null)
            //
            JSONArray stages = loanConfig.containsKey('StageCount') ? loanConfig.getJSONArray('StageCount') : null
            String loanProductId = ''
            for (int i = 0; i < stages.size(); i++) {
                JSONObject stage = stages.getJSONObject(i)
                if (stage.containsKey('value') && stage.containsKey('key')) {
                    if (stage.getString('value') == loanTerm) {
                        loanProductId = stage.getString('key')
                    }
                }
            }
            //
            JSONObject firstSubmitObj = new JSONObject() {
                {
                    put('C_STEP_ID', 'NYB01_01');
                    put('C_FORM_ID', 'NYB01');
                    put('C_ITEM_NAME', productName);
                    put('N_AMT_APPLIED', amount);
                    put('N_GEEX_LOAN_PDT_ID', loanProductId);
                }
            }
            AppCommonResult appCommonResult = submitApplyStep(appToken, firstSubmitObj)
            if (!appCommonResult.result) {
                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            JSONObject firstSubmit = JSON.parseObject(JSON.toJSONString(appCommonResult.responseObject))
            String appId = firstSubmit.containsKey('C_APP_ID') ? firstSubmit.getString('C_APP_ID') : ''
            Map<String, Object> appIdForm = new HashMap() {
                {
                    put('appId', appId)
                    put('loanTerm', loanTerm)
                }
            }
            CamundaService.completeTask(taskId, appIdForm)
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('loanTerm error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[loan_term]系统错误，联系管理员')
    }
}

// 加载用户信息
// service task
static def loadClientIdentity(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    //
    String appToken = getAppToken(mobile, null, null)
    // call load identity
    AppCommonResult appCommonResult = loadIdentity(appId, appToken)
    //
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(false, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    }
    //
    Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
    result.put('afterFunction', afterFunctionMap)
    result.put('s_taskResult', appCommonResult.result)
    return result
}

static def doIdCardOcr(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String url = ''
    String fileType = ''
    if ('id_photo_front' == method) {
        url = args.containsKey('idPhotoFrontUrl') ? args.getString('idPhotoFrontUrl') : ''
        fileType = 'idnoFront'
    } else if ('id_photo_back' == method) {
        url = args.containsKey('idPhotoBackUrl') ? args.getString('idPhotoBackUrl') : ''
        fileType = 'idnoBack'
    }
    if (StrUtil.isEmpty(url) || StrUtil.isEmpty(fileType)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '[' + method + ']没有照片參數')
    }
    //
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('doIdCardOcr no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[' + method + ']获取当前流程失败，请联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[' + method + ']当前流程操作失败，请联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appToken = getAppToken(mobile, null, null)
            AppCommonResult appCommonResult = doIdCardOcr(url, fileType, appToken)
            if (!appCommonResult.result) {
                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            JSONObject ocrResult = JSON.parseObject(JSON.toJSONString(appCommonResult.responseObject))
            Map<String, Object> inputForm = new HashMap() {
                {
                    put('ocr' + fileType, ocrResult)
                }
            }
            CamundaService.completeTask(taskId, inputForm)
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('doIdCardOcr error', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[' + method + ']系统错误，请联系管理员')
    }
}
// service task
static def bankCodeConfig(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    AppCommonResult appCommonResult = doBankCode(['action': 'cardPackage'])
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    }
    //
    result.putAll(JSONObject.parseObject(JSON.toJSONString(appCommonResult.responseObject)))
    //
    Map<String, Object> afterFunctionMap = noticeMap(true, '', data)
    result.put('afterFunction', afterFunctionMap)
    result.put('s_taskResult', true)
    return result
}

static def bankCard(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    String bankMobile = args.containsKey('bankMobile') ? args.getString('bankMobile') : ''
    String bankName = args.containsKey('bankName') ? args.getString('bankName') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId);
        if (taskReturn == null) {
            logger.error('bankCard no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[back_card]无法获取当前流程，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[back_card]当前流程操作失败，请联系管理员')
            }
            // transfer bankName to bankCode
            String bankCode = bankName
            CamundaService.getProcessVariable(taskReturn.getProcessInstanceId()).getJSONObject('bankCodeConfig').forEach {
                String key, String value ->
                    if (key == bankName) {
                        bankCode = value
                    }
            }
            String taskId = taskReturn.getTaskId()
            Map<String, Object> inputForm = new HashMap() {
                {
                    put('bankCard', bankCard)
                    put('bankMobile', bankMobile)
                    put('bankCode', bankCode)
                }
            }
            CamundaService.completeTask(taskId, inputForm)
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('bankCard error,', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[back_card]系统错误，联系管理员')
    }
}
// service task
static def checkBankCard(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
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
    //
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())
    // check bank card limit
    AppCommonResult appCommonResult = checkBankCardLimit(appToken, name, idNo, bankCard, amount)
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    }
    // check old identity
    appCommonResult = checkOldIdentity(appToken, name, idNo)
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    } else {
        JSONObject responseObject = JSON.parseObject(JSON.toJSONString(appCommonResult.responseObject))
        boolean needVerify = responseObject.getBooleanValue('needVerify')
        result.put('needOldIdentity', needVerify)
    }
    // check pay protocol
    appCommonResult = checkPayProtocol(appToken, appId, name, idNo, bankCard, bankMobile, bankCode) as AppCommonResult
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    }
    JSONObject checkProtocolResult = JSON.parseObject(JSON.toJSONString(appCommonResult.responseObject))
    String protocolKey = checkProtocolResult.containsKey('makeProtocolKey') ? checkProtocolResult.getString('makeProtocolKey') : ''
    boolean needMakeProtocol = checkProtocolResult.containsKey('needMakeProtocol') ? checkProtocolResult.getBooleanValue('needMakeProtocol') : false
    //
    Map<String, Object> afterFunctionMap = noticeMap(data)
    result.put('needMakeProtocol', needMakeProtocol)
    result.put('payProtocolKey', protocolKey)
    result.put('afterFunction', afterFunctionMap)
    result.put('s_taskResult', true)
    return result
}

// 提交用户支付协议
static def submitPayProtocol(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String verifyCode = args.containsKey('payProtocolVerifyCode') ? args.getString('payProtocolVerifyCode') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('submitUserPayProtocol no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[submit_pay_protocol]无法获取当前流程，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[submit_pay_protocol]流程操作失败，联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            // needMakeProtocol
            boolean needMakeProtocol = processVariable.containsKey('needMakeProtocol') ? processVariable.getBooleanValue('needMakeProtocol') : false
            if (!needMakeProtocol) {
                logger.info('submitUserPayProtocol no need make protocol')
                CamundaService.completeTask(taskId, [:])
                return ResponseVo.makeSuccess(null)
            }
            //
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appId = processVariable.containsKey('appId') ? processVariable.getString('appId') : ''
            String payProtocolKey = processVariable.containsKey('payProtocolKey') ? processVariable.getString('payProtocolKey') : ''
            String bankCard = processVariable.containsKey('bankCard') ? processVariable.getString('bankCard') : ''
            String bankMobile = processVariable.containsKey('bankMobile') ? processVariable.getString('bankMobile') : ''
            String bankCode = processVariable.containsKey('bankCode') ? processVariable.getString('bankCode') : ''
            String storeCode = processVariable.containsKey('storeCode') ? processVariable.getString('storeCode') : ''
            JSONObject ocrFront = processVariable.containsKey('ocridnoFront') ? processVariable.getJSONObject('ocridnoFront') : null
            JSONObject ocrFrontDetail = ocrFront.containsKey('ocrDetail') ? ocrFront.getJSONObject('ocrDetail') : null
            String name = ocrFrontDetail.containsKey('name') ? ocrFrontDetail.getString('name') : ''
            String idNo = ocrFrontDetail.containsKey('idCardNumber') ? ocrFrontDetail.getString('idCardNumber') : ''
            JSONObject ocrBack = processVariable.containsKey('ocridnoBack') ? processVariable.getJSONObject('ocridnoBack') : null
            JSONObject ocrBackDetail = ocrBack.containsKey('ocrDetail') ? ocrBack.getJSONObject('ocrDetail') : null
            String idValid = ocrBackDetail.containsKey('validDate') ? ocrBackDetail.getString('validDate') : ''
            String appToken = getAppToken(mobile, null, null)
            AppCommonResult appCommonResult = submitPayProtocol(appId, bankCode, bankMobile, verifyCode, payProtocolKey, bankCard, appToken)
            if (!appCommonResult.result) {
                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            logger.info('submitPayProtocol result: {}', appCommonResult)
            //
            appCommonResult = submitIdentity(appId, name, idNo, idValid, bankCode, storeCode, bankCard, bankMobile, appToken)
            if (!appCommonResult.result) {
                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            CamundaService.completeTask(taskId, [:])
            return ResponseVo.makeSuccess(appCommonResult.responseObject)
        }
    } catch (Exception ex) {
        logger.error('processPayProtocol error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[submit_pay_protocol]系统错误，联系管理员')
    }
}
// service task
static def contractPreviewNotice(String arguments) {
    JSONObject args = JSON.parseObject(arguments)

    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String botId = args.containsKey('botId') ? args.getString('botId') : ''
    String accountId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String name = args.containsKey('name') ? args.getString('name') : '' // 姓名
    String idNo = args.containsKey('idNo') ? args.getString('idNo') : '' // 身份证
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : '' // 手机号
    String amt = args.containsKey('applyAmount') ? args.getString('applyAmount') : ''
    // 贷款金额
    String tenor = args.containsKey('loanTerm') ? args.getString('loanTerm') : '' // 贷款期数
    String addr = args.containsKey('address') ? args.getString('address') : '' // 地址
    String bankCard = args.containsKey('bankCard') ? args.getString('bankCard') : ''
    // 银行卡号
    String userPoint = args.containsKey('userPoint') ? args.getString('userPoint') : ''
    // 月利率
    String amtTenor = args.containsKey('amtTenor') ? args.getString('amtTenor') : ''
    // 每月还款
    String itemName = args.containsKey('productName') ? args.getString('productName') : ''
    // 产品名称
    String product = args.containsKey('productId') ? args.getString('productId') : ''
    // 产品id
    String icName = args.containsKey('icName') ? args.getString('icName') : '' // 工商名称
    Map<String, Object> inputForm = new HashMap() {
        {
            put('name', name)
            put('idNo', idNo)
            put('mobile', mobile)
            put('amt', amt)
            put('tenor', tenor)
            put('addr', addr)
            put('bankcard', bankCard)
            put('userPoint', userPoint)
            put('amtTenor', amtTenor)
            put('itemName', itemName)
            put('product', product)
            put('icName', icName)
        }
    }
    JSONObject data = noticeData(botId, accountId, '', '', 'contract', inputForm)
    Map<String, Object> afterFunctionMap = noticeMap(data)
    return new JSONObject() {
        {
            put('afterFunction', afterFunctionMap)
            put('s_taskResult', true)
        }
    }
}

static def contractPreview(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('contractPreview no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[contract_preview]获取当前流程失败，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[contract_preview]流程操作失败，联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            CamundaService.completeTask(taskId, [:])
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('contractPreview error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[contract_preview]系统错误，联系管理员')
    }
}
// service task
static def secondStep(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
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
    AppCommonResult appCommonResult = submitApplyStep(appToken, info)
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())
    //
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    }
    //
    Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
    result.put('afterFunction', afterFunctionMap)
    result.put('s_taskResult', true)
    return result
}

static def maritalStatus(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String marital = args.containsKey('marital') ? args.getString('marital') : ''
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('maritalStatus no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[marital_status]获取当前流程失败， 联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[marital_status]操作流程失败， 联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            String maritalKey = marital
            for (String key : maritalStatusMap.keySet()) {
                if (key == marital) {
                    maritalKey = maritalStatusMap.get(key)
                }
            }
            Map<String, Object> inputForm = new HashMap() {
                {
                    put('maritalKey', maritalKey)
                }
            }
            CamundaService.completeTask(taskId, inputForm)
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('maritalStatus error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[marital_status]系统错误，联系管理员')
    }
}

static def relationInfo(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String relationName = args.containsKey('relationName') ? args.getString('relationName') : '' // 联系人
    String relationTel = args.containsKey('relationTel') ? args.getString('relationTel') : '' // 联系人电话
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('relationInfo no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[relation_info]获取当前流程失败， 联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[relation_info]操作流程失败， 联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            Map<String, Object> inputForm = new HashMap() {
                {
                    put('relationName', relationName)
                    put('relationTel', relationTel)
                }
            }
            CamundaService.completeTask(taskId, inputForm)
            return ResponseVo.makeSuccess(null)
        }
    } catch (Exception ex) {
        logger.error('relationInfo error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[relation_info]系统错误，联系管理员')
    }
}

static def thirdStep(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String relation = args.containsKey('relation') ? args.getString('relation') : '' // 联系人关系
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('thirdStep no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[third_step]获取当前流程失败， 联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[third_step]操作流程失败， 联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appId = processVariable.containsKey('appId') ? processVariable.getString('appId') : ''
            String maritalKey = processVariable.containsKey('maritalKey') ? processVariable.getString('maritalKey') : ''
            String relationName = processVariable.containsKey('relationName') ? processVariable.getString('relationName') : ''
            String relationTel = processVariable.containsKey('relationTel') ? processVariable.getString('relationTel') : ''
            String relationKey = relation
            for (String key : relationMap.keySet()) {
                if (key == relation) {
                    relationKey = relationMap.get(key)
                }
            }
            //
            JSONObject stepInputForm = new JSONObject() {
                {
                    put('C_APP_ID', appId)
                    put('C_MARITAL', maritalKey)
                    put('C_STEP_ID', 'NYB01_03')
                    put('C_RELATION', relationKey)
                    put('C_CONTACT_NM', relationName)
                    put('C_CTAT_TEL_CELL', relationTel)
                }
            }
            String appToken = getAppToken(mobile, null, null)
            AppCommonResult appCommonResult = submitApplyStep(appToken, stepInputForm)
            if (!appCommonResult.result) {
                Map<String, Object> inputForm = new HashMap() {
                    {
                        put('step_3_result', false)
                    }
                }
                CamundaService.completeTask(taskId, inputForm)

                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            Map<String, Object> inputForm = new HashMap() {
                {
                    put('relationKey', relationKey)
                    put('step_3_result', true)
                }
            }
            CamundaService.completeTask(taskId, inputForm)
            return ResponseVo.makeSuccess(appCommonResult.responseObject)
        }
    } catch (Exception ex) {
        logger.error('thirdStep error,', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[third_step]系统错误， 联系管理员')
    }
}

static def forthStep(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    String companyName = args.containsKey('companyName') ? args.getString('companyName') : ''
    String mailAddr = args.containsKey('mailAddr') ? args.getString('mailAddr') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('thirdStep no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[forth_step]当前流程失败， 联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[forth_step]操作流程失败， 联系管理员')
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
            AppCommonResult appCommonResult = submitApplyStep(appToken, stepInputForm)
            if (!appCommonResult.result) {
                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            //
            CamundaService.completeTask(taskId, [:])
            return ResponseVo.makeSuccess(appCommonResult.responseObject)
        }
    } catch (Exception ex) {
        logger.error('forthStep error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[forth_step]系统错误， 联系管理员')
    }
}
// service task
static def faceDetect(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    String externalId = args.containsKey('externalId') ? args.getString('externalId') : ''
    String appToken = getAppToken(mobile, null, null)
    AppCommonResult appCommonResult = doFaceCheck(appToken, ['appId': appId])
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())
    if (!appCommonResult.result) {
        Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    }
    //
    JSONObject detectResult = JSON.parseObject(JSON.toJSONString(appCommonResult.responseObject))
    if (detectResult.containsKey('needFace') && detectResult.getIntValue('needFace') == 1) {
        // 需要人脸识别
        String redirectUrl = gonggongUrl + '/geexSmartRobot/robot/' + externalId
        JSONObject params = new JSONObject() {
            {
                put('appId', appId);
                put('appFrom', '1');
                put('redirectUrl', redirectUrl);
                put('videoType', '1');
            }
        }
        appCommonResult = doWebankFace(appToken, params)
        if (!appCommonResult.result) {
            Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
            result.put('afterFunction', afterFunctionMap)
            result.put('s_taskResult', false)
            return result
        }
        JSONObject faceResult = JSON.parseObject(JSON.toJSONString(appCommonResult.responseObject))
        //
        data = noticeData(args, '', '', 'redirect', faceResult)
        Map<String, Object> afterFunctionMap = noticeMap(true, '', data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', true)
        return result
    }
}

static def faceVerify(String method, String arguments) {
    JSONObject args = JSON.parseObject(arguments)

    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    try {
        TaskReturn taskReturn = CamundaService.queryCurrentTask(userId)
        if (taskReturn == null) {
            logger.error('faceValidate no current task, businessKey: {}', userId)
            return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_PROCESS_MISSING, '[face_validate]当前流程失败，联系管理员')
        } else {
            if (!checkTaskPosition(method, taskReturn.getTaskName())) {
                return ResponseVo.makeFail(GeneralCodes.PROCESS_FAILED_TASK_NOT_MATCH, '[face_validate]操作流程失败，联系管理员')
            }
            String taskId = taskReturn.getTaskId()
            String processInstanceId = taskReturn.getProcessInstanceId()
            JSONObject processVariable = CamundaService.getProcessVariable(processInstanceId)
            String code = processVariable.containsKey('code') ? processVariable.getString('code') : ''
            String orderNo = processVariable.containsKey('orderNo') ? processVariable.getString('orderNo') : ''
            String h5faceId = processVariable.containsKey('h5faceId') ? processVariable.getString('h5faceId') : ''
            String signature = processVariable.containsKey('signature') ? processVariable.getString('signature') : ''
            String newSignature = processVariable.containsKey('newSignature') ? processVariable.getString('newSignature') : ''
            String liveRate = processVariable.containsKey('liveRate') ? processVariable.getString('liveRate') : ''
            String type = processVariable.containsKey('type') ? processVariable.getString('type') : ''
            String mobile = processVariable.containsKey('mobile') ? processVariable.getString('mobile') : ''
            String appToken = getAppToken(mobile, null, null)
            JSONObject params = new JSONObject() {
                {
                    put('code', code)
                    put('orderNo', orderNo)
                    put('h5faceId', h5faceId)
                    put('signature', signature)
                    put('newSignature', newSignature)
                    put('liveRate', liveRate)
                    put('type', type)
                }
            }
            AppCommonResult appCommonResult = validateH5Face(appToken, params)
            if (!appCommonResult.result) {
                CamundaService.completeTask(taskId, ['face_result': false])
                return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_RESULT_INVALID, appCommonResult.errMsg)
            }
            //
            CamundaService.completeTask(taskId, ['face_result': true])
            return makeResponseVo(true, null, appCommonResult.responseObject)
        }
    } catch (Exception ex) {
        logger.error('faceValidate error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[face_validate]系统错误， 联系管理员')
    }
}
// service task
static def submitAudit(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    JSONObject result = new JSONObject()
    String appId = args.containsKey('appId') ? args.getString('appId') : ''
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    String appToken = getAppToken(mobile, null, null)
    JSONObject info = new JSONObject() {
        {
            put('C_APP_ID', appId)
            put('C_STEP_ID', 'PREVIEW')
        }
    }
    AppCommonResult appCommonResult = submitApplyStep(appToken, info)
    JSONObject data = noticeData(args, '', '', '', new HashMap<String, Object>())

    if (!appCommonResult.result) {
        logger.error('submit_audit error, {}', appCommonResult.errMsg)
        Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
        result.put('afterFunction', afterFunctionMap)
        result.put('s_taskResult', false)
        return result
    }
    Map<String, Object> afterFunctionMap = noticeMap(appCommonResult.result, appCommonResult.errMsg, data)
    result.put('afterFunction', afterFunctionMap)
    result.put('s_taskResult', true)
    return result
}
// service task
static def noticeHub(String arguments) {
    JSONObject args = JSON.parseObject(arguments) // notice response
    Boolean success = args.containsKey('success') ? args.getBooleanValue('success') : true
    String msg = args.containsKey('msg') ? args.getString('msg') : ''
    JSONObject data = args.containsKey('data') ? args.getJSONObject('data') : null

    noticeHub(success, msg, data)
}

static def deleteLoanProcess(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String userId = args.containsKey('accountid') ? args.getString('accountid') : ''
    try {
        CamundaService.deleteProcess(userId)
        return ResponseVo.makeSuccess(null)
    } catch (Exception ex) {
        logger.error('deleteLoanProcess error, ', ex)
        return ResponseVo.makeFail(GeneralCodes.LOGIC_EXCEPTION, '[delete_loan_process]后台错误，联系管理员')
    }
}


// ####################################### 非 function 方法 #################################################
// 登录app
static def loginApp(String userMobile, String verifyCode, String deviceId) {
    Map<String, Object> params = new HashMap() {
        {
            put('deviceId', deviceId)
            put('account', userMobile)
            put('veriCode', verifyCode)
        }
    }
    String token = '';
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, loginAppPath, params, null)
        if (response == null) {
            logger.error('user login App no response')
            return token;
        }
        logger.info('user login App response: {}', response.body())
        if (response.isOk()) {
            AppCommonResult appCommonResult = JSON.parseObject(response.body(), AppCommonResult.class)
            if (appCommonResult.result) {
                JSONObject responseObject = JSON.toJSON(appCommonResult.responseObject) as JSONObject
                token = responseObject.getString('token')
                String appTokenKey = APP_TOKEN_KEY + userMobile
                // save token key
                StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class)
                stringRedisTemplate.opsForValue().set(appTokenKey, token, 3600, TimeUnit.SECONDS)
            } else {
                logger.error('user login App failed, result: {}', appCommonResult.errMsg)
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
    // 通用验证码
//    if (StrUtil.isEmptyIfStr(appToken) && StrUtil.isEmptyIfStr(verifyCode)) {
//        String commonVerifyCode = getCommonVerifyCode()
//        if (StrUtil.isEmptyIfStr(commonVerifyCode)) {
//            return appToken
//        }
//        appToken = getAppToken(mobile, commonVerifyCode, deviceId)
//    }
    return appToken
}

static def getCommonVerifyCode() {
    HttpResponse response = RestClient.doGet(dingdanUrl, getCommonVerifyCodePath, null, null)
    if (response == null) {
        logger.error("getCommonVerifyCode no response")
        return null
    }
    if (response.ok && !StrUtil.isEmpty(response.body())) {
        AppCommonResult appCommonResult = JSON.parseObject(response.body(), AppCommonResult.class)
        return appCommonResult.responseObject as String
    }
    return null
}

// 发送登录验证码
static def sendLoginSms(String userMobile) {

    JSONObject result = new JSONObject()
    int randomInt = RandomUtil.getSecureRandom().nextInt(10)
    String checkKey = DigestUtil.md5Hex(('verify_code_check' + userMobile + randomInt).getBytes())
    Map<String, Object> params = new HashMap() {
        {
            put('mobile', userMobile)
            put('random', String.valueOf(randomInt))
            put('requestKey', checkKey)
        }
    }
    HttpResponse response = RestClient.doPostWithBody(dingdanUrl, getAppVerifyCodeCheckPath, params, null)
    if (response == null) {
        logger.error('send login sms no response')
        return new AppCommonResult(false, '短信验证码发送失败', 1, 0, result)
    }
    logger.info('send login sms response: {}', response.body())
    if (response.ok && !StrUtil.isEmpty(response.body())) {
        return JSON.parseObject(response.body(), AppCommonResult.class)
    }
    return new AppCommonResult(false, '短信验证码发送失败', 1, 0, result)
}

static def preApply(String token, String mobile) {
    JSONObject result = new JSONObject()
    // post params body
    Map<String, Object> params = new HashMap() {
        {
            put('mobile', mobile)
            put('showLoanList', 'DDG')
        }
    }
    // post headers
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, preApplyPath + token, params, requestAuth)
        if (response == null) {
            logger.error('preApply no response')
            return new AppCommonResult(false, '没有取得门店配置，联系管理员', 1, 0, result)
        }
        logger.info('preApply response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('preApply error,', ex)
    }
    return new AppCommonResult(false, '没有取得门店配置，联系管理员', 1, 0, result)
}

static def loadIdentity(String appId, String token) {
    JSONObject result = new JSONObject()
    // post params body
    Map<String, Object> params = new HashMap() {
        {
            put('appId', appId)
        }
    }
    // post headers
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, loadIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error('loadIdentity no response')
            return new AppCommonResult(false, '加载用户信息失败', 1, 0, result)
        }
        logger.info('loadIdentity response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('loadIdentity error,', ex)
    }
    return new AppCommonResult(false, '加载用户信息失败', 1, 0, result)
}

// 识别身份证
static def doIdCardOcr(String url, String fileType, String token) {
    JSONObject result = new JSONObject()
    // file dest
    File file = File.createTempFile('idPhoto', '.ycImage')
    // download file
    HttpUtil.downloadFile(url, file)
    // read file to bytes
    byte[] fileContent = FileUtil.readBytes(file)
    // base64 encoding
    String encodedString = Base64.getEncoder().encodeToString(fileContent)
    // post params body
    Map<String, Object> params = new HashMap() {
        {
            put('fileType', fileType)
            put('imgData', 'data:image/jpeg;base64,' + encodedString)
        }
    }
    // post header
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithForm(dingdanUrl, doIdCardOcrPath + token, params, requestAuth)
        if (response == null) {
            logger.error('doIdCardOcr no response')
            return new AppCommonResult(false, '身份证识别失败', 1, 0, result)
        }
        logger.info('doIdCardOcr response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('doIdCardOcr error,', ex)
    }
    return new AppCommonResult(false, '身份证识别失败', 1, 0, result)
}

static def doBankCode(Map<String, Object> params) {
    JSONObject result = new JSONObject()
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, getSupportBankListPath, params, null)
        if (response == null) {
            logger.error('bankCodeConfig no response')
            return new AppCommonResult(false, '获取银行列表失败', 1, 0, result)
        }

        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            AppCommonResult appCommonResult = JSON.parseObject(response.body(), AppCommonResult.class)

            JSONObject json = JSON.toJSON(appCommonResult.responseObject) as JSONObject
            if (json != null && !json.isEmpty()) {
                JSONArray allBanks = json.containsKey('allBanks') ? json.getJSONArray('allBanks') : null
                if (allBanks != null && !allBanks.isEmpty()) {
                    Map<String, String> bankMap = new HashMap()
                    for (int i = 0; i < allBanks.size(); i++) {
                        JSONObject bank = allBanks.getJSONObject(i)
                        String bankCode = bank.containsKey('bankCode') ? bank.getString('bankCode') : ''
                        String bankName = bank.containsKey('bankName') ? bank.getString('bankName') : ''
                        bankMap.put(bankName, bankCode)
                    }
                    if (!bankMap.isEmpty()) {
                        result.put('bankCodeConfig', bankMap)
                        return new AppCommonResult(true, '', 1, 1, result)
                    }
                }
            }
        }
    } catch (Exception ex) {
        logger.error('bankCodeConfig error', ex)
    }
    return new AppCommonResult(false, '获取银行列表失败', 1, 0, result)
}

// check bank card limit
static def checkBankCardLimit(String token, String name, String idNo, String bankCard, Integer amount) {
    JSONObject result = new JSONObject()
    // post params body
    Map<String, Object> params = new HashMap() {
        {
            put('name', name)
            put('idNo', idNo)
            put('bankCard', bankCard)
            put('loanAmt', amount)
        }
    }
    // post headers
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, checkBankCardLimitPath, params, requestAuth)
        if (response == null) {
            logger.error('checkBankCardLimit no response')
            return new AppCommonResult(false, '银行卡限额校验失败', 1, 0, result)
        }
        logger.info('checkBankCardLimit response:{}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            AppCommonResult appCommonResult = JSON.parseObject(response.body(), AppCommonResult.class)
            //
            if (appCommonResult.result) {
                return new AppCommonResult(true, '银行卡限额校验通过', 1, 1, result)
            }
        }
    } catch (Exception ex) {
        logger.error('checkBankCardLimit error,', ex)
    }
    return new AppCommonResult(false, '银行卡限额校验失败', 1, 0, result)
}

// check 老客户信息
static def checkOldIdentity(String token, String name, String idNo) {
    JSONObject result = new JSONObject()
    Map<String, Object> params = new HashMap() {
        {
            put('name', name)
            put('idNo', idNo)
        }
    }
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, checkOldIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error('checkIdentity no response')
            return new AppCommonResult(false, '老客户信息检测失败', 1, 0, result)
        }
        logger.info('checkIdentity response:{}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('checkIdentity error,', ex)
    }
    return new AppCommonResult(false, '老客户信息检测失败', 1, 0, result)
}

// check 用户支付协议
static def checkPayProtocol(String token, String appId, String name, String idNo, String bankCard, String bankMobile, String bankCode) {
    JSONObject result = new JSONObject()
    // pageUrl 签约回调地址，不是招商银行，可以为空
    Map<String, Object> params = new HashMap() {
        {
            put('appId', appId)
            put('name', name)
            put('idNo', idNo)
            put('bankCode', bankCode)
            put('accountId', bankCard)
            put('rsvPhone', bankMobile)
            put('type', '1')
            put('pageUrl', '')
        }
    }
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, checkProtocolPath + token, params, requestAuth)
        if (response == null) {
            logger.error('checkUserPayProtocol no response')
            return new AppCommonResult(false, '用户支付协议检测失败', 1, 0, result)
        }
        logger.info('checkUserPayProtocol response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('checkUserPayProtocol error,', ex)
    }
    return new AppCommonResult(false, '用户支付协议检测失败', 1, 0, result)
}

static def submitPayProtocol(String appId, String bankCode, String bankMobile, String verifyCode, String payProtocolKey, String bankCard, String token) {
    JSONObject result = new JSONObject()
    // post params body
    Map<String, Object> params = new HashMap() {
        {
            put('appId', appId)
            put('cardCode', bankCode)
            put('reservedMobile', bankMobile)
            put('smsStr', verifyCode)
            put('makeProtocolKey', payProtocolKey)
            put('cardNo', bankCard)
            put('type', '1')
        }
    }
    // post headers
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, submitProtocolPath + token, params, requestAuth)
        if (response == null) {
            logger.error('submitUserPayProtocol no response')
            return new AppCommonResult(false, '提交用户支付协议失败，联系管理员', 1, 0, result)
        }
        logger.info('submitUserPayProtocol response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('submitUserPayProtocol error,', ex)
    }
    return new AppCommonResult(false, '提交用户支付协议失败，联系管理员', 1, 0, result)
}

static def submitIdentity(String appId, String name, String idNo, String idValid, String bankCode,
                          String storeCode, String bankCard, String bankMobile, String token) {
    JSONObject result = new JSONObject()
    // post params body
    Map<String, Object> params = new HashMap() {
        {
            put('C_APP_ID', appId)
            put('name', name)
            put('C_ID_VALID', idValid)
            put('idNo', idNo)
            put('bankCode', bankCode)
            put('storeCode', storeCode)
            put('accountId', bankCard)
            put('rsvPhone', bankMobile)
        }
    }
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, submitIdentityPath + token, params, requestAuth)
        if (response == null) {
            logger.error('submitIdentity no response')
            return new AppCommonResult(false, '提交身份信息失败，联系管理员', 1, 0, result)
        }
        logger.info('submitIdentity response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('submitIdentity error,', ex)
    }
    return new AppCommonResult(false, '提交身份信息失败，联系管理员', 1, 0, result)
}

// 提交申请
static def submitApplyStep(String token, JSONObject info) {
    JSONObject result = new JSONObject()
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, submitApplyStepPath + token, info, requestAuth)
        if (response == null) {
            logger.error('submitApplyStep no response')
            return new AppCommonResult(false, '进件提交订单信息失败，联系管理员', 1, 0, result)
        }
        logger.info('submitApplyStep response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('submitApplyStep error,', ex)
    }
    return new AppCommonResult(false, '进件提交订单信息失败，联系管理员', 1, 0, result)
}

static def doFaceCheck(String token, Map<String, Object> params) {
    JSONObject result = new JSONObject()
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithForm(dingdanUrl, faceDetectPath, params, requestAuth)
        if (response == null) {
            logger.error('doFaceCheck no response')
            return new AppCommonResult(false, '检测活体人脸启动失败', 1, 0, result)
        }
        logger.info('doFaceCheck response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('doFaceCheck error,', ex)
    }
    return new AppCommonResult(false, '检测活体人脸启动失败', 1, 0, result)
}

static def doWebankFace(String token, JSONObject params) {
    JSONObject result = new JSONObject()
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, webankH5Path, params, requestAuth)
        if (response == null) {
            logger.error('doWebankFace no response')
            return new AppCommonResult(false, '启动活体人脸失败', 1, 0, result)
        }
        logger.info('doWebankFace response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('doWebankFace error,', ex)
    }
    return new AppCommonResult(false, '启动活体人脸失败', 1, 0, result)
}

static def validateH5Face(String token, JSONObject params) {
    JSONObject result = new JSONObject()
    Map<String, String> headers = wrapHeadersWithToken(token)
    RequestAuth requestAuth = new RequestAuth(null, null, null, headers)
    try {
        HttpResponse response = RestClient.doPostWithBody(dingdanUrl, validateH5FacePath, params, requestAuth)
        if (response == null) {
            logger.error('validateH5Face no response')
            return new AppCommonResult(false, '人脸验证提交失败， 联系管理员', 1, 0, result)
        }
        logger.info('validateH5Face response: {}', response.body())
        if (response.isOk() && !StrUtil.isEmpty(response.body())) {
            return JSON.parseObject(response.body(), AppCommonResult.class)
        }
    } catch (Exception ex) {
        logger.error('validateH5Face error, ', ex)
    }
    return new AppCommonResult(false, '人脸验证提交失败， 联系管理员', 1, 0, result)
}


static def wrapHeadersWithToken(String token) {
    Map<String, String> defaultHeaders = new HashMap() {
        {
            put('platform', 'wechat')
        }
    }
    if (!StrUtil.isEmptyIfStr(token)) {
        defaultHeaders.put('appToken', token)
    }
    return defaultHeaders
}

static def checkTaskPosition(String method, String taskName) {
    return method == taskName
}

static def makeResponseVo(boolean success, String msg, Object data) {
    return new JSONObject() {
        {
            put('success', success)
            put('code', null)
            put('msg', msg)
            put('data', data)
        }
    }
}

static def noticeMap(JSONObject noticeData) {
    return noticeMap(new JSONObject(), noticeData)
}

static def noticeMap(JSONObject responseData, JSONObject noticeData) {
    Boolean success = responseData.containsKey('success') ? responseData.getBooleanValue('success') : true
    String msg = responseData.containsKey('msg') ? responseData.getString('msg') : ''
    return noticeMap(success, msg, noticeData)
}

static def noticeMap(boolean success, String msg, JSONObject noticeData) {
    JSONObject noticeArgs = makeResponseVo(success, msg, noticeData)
    return new HashMap<String, Object>() {
        {
            put('notice_hub', noticeArgs)
        }
    }
}

static def noticeData(JSONObject args) {
    String botId = args.containsKey('botId') ? args.getString('botId') : ''
    String accountId = args.containsKey('accountId') ? args.getString('accountId') : ''
    String content = args.containsKey('content') ? args.getString('content') : ''
    String picUrl = args.containsKey('picUrl') ? args.getString('picUrl') : ''
    String type = args.containsKey('type') ? args.getString('type') : ''
    Map<String, Object> param = args.containsKey('param') ? args.get('param') as Map<String, Object> : new HashMap<String, Object>()
    return noticeData(botId, accountId, content, picUrl, type, param)
}

static def noticeData(JSONObject processVariables, String content, String picUrl, String type, Map<String, Object> param) {
    String botId = processVariables.containsKey('botId') ? processVariables.getString('botId') : ''
    String accountId = processVariables.containsKey('accountId') ? processVariables.getString('accountId') : ''
    return noticeData(botId, accountId, content, picUrl, type, param)
}

static def noticeData(String botId, String accountId, String content, String picUrl, String type, Map<String, Object> param) {
    return new JSONObject() {
        {
            put('botId', botId)
            put('accountId', accountId)
            put('content', content)
            put('picUrl', picUrl)
            put('type', type)
            put('param', param)
        }
    }
}

static def noticeHub(JSONObject responseVo) {
    HttpResponse response = RestClient.doPostWithBody(gonggongUrl, noticeHubPath, responseVo, null)
    if (!StrUtil.isEmpty(response.body())) {
        logger.info('noticeHub response {}', response.body())
    }
}

static def noticeHub(boolean success, String msg, JSONObject noticeData) {
    JSONObject params = makeResponseVo(success, msg, noticeData)
    noticeHub(params)
}

class AppCommonResult {
    boolean result = true
    String errMsg = ''
    Integer status = 1
    Integer success = 1
    Object responseObject

    AppCommonResult() {
    }

    AppCommonResult(boolean result, String errMsg, Integer status, Integer success, Object responseObject) {
        this.result = result
        this.errMsg = errMsg
        this.status = status
        this.success = success
        this.responseObject = responseObject
    }
}