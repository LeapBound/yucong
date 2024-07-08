package scripts.cashloan

import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpResponse
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.ResponseVo
import com.github.leapbound.yc.action.func.groovy.RestClient
import groovy.transform.Field
import scripts.alpha.Alpha
import scripts.general.GeneralCodes
import scripts.general.GeneralMethods

import java.math.RoundingMode

/**
 *
 * @author yamath
 * @since 2023/10/16 13:27
 */
// geex-guts-hub 地址
@Field static String gutsHubUrl = 'https://beta.geexfinance.com/geex-guts-hub'
@Field static String gonggongUrl = ''
// cash 申请贷款 url
@Field static String applySubmitPath = '/cash/loan/apply/submit'
// cash 查询贷款进度 url
@Field static String applyAuditStatusPath = '/cash/loan/apply/audit/status'
// cash 绑定银行卡 url
@Field static String bindBankCardPath = '/cash/loan/bank/card/bind'
// cash 绑定银行卡验证码 url
@Field static String bindBankCardCaptchaPath = '/cash/loan/bank/card/bind/captcha'
// cash 提现放款 url
@Field static String loanSubmitPath = '/cash/loan/loan/submit'

// exec method
execCashLoanMethod(method, arguments)

/**
 * exec cash 方法
 * @param method function method
 * @param arguments 入参
 * @return JSONObject
 */
static def execCashLoanMethod(String method, String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // check arguments
    if (StrUtil.isEmptyIfStr(arguments)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供必要的信息')
    }
    // get external args
    gonggongUrl = GeneralMethods.getExternal(arguments).get('gonggongUrl')
    Alpha.alphaLoginUrl = gonggongUrl
    //
    switch (method) {
        case 'apply_loan': // 申请贷款
            result = applyLoan(arguments)
            break
        case 'apply_audit_status': // 查询贷款进度
            result = applyAuditStatus(arguments)
            break
        case 'bind_card': // 绑定银行卡
            result = bindCard(arguments)
            break
        case 'bind_card_captcha': // 绑定银行卡验证码
            result = bindCardCaptcha(arguments)
            break
        case 'loan_submit': // 放款
            result = loanSubmit(arguments)
            break
        default: // no method exist
            result = ResponseVo.makeFail(GeneralCodes.MISSING_EXEC_METHOD, '没有对应的执行方法')
            break
    }
    return result
}

/**
 * 申请贷款
 * @param arguments 入参
 * @return JSONObject
 */
static def applyLoan(String arguments) {
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    //
    String name = args.containsKey('name') ? args.getString('name') : ''
    String idNo = args.containsKey('idNo') ? args.getString('idNo') : ''
    String mobile = args.containsKey('mobile') ? args.getString('mobile') : ''
    // check
    if (StrUtil.isBlankIfStr(name)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供姓名')
    }
    if (StrUtil.isBlankIfStr(idNo)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供身份证')
    }
    if (StrUtil.isBlankIfStr(mobile)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供手机号')
    }
    // check confirm
    if (!args.containsKey('confirm')) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '请仔细确认提供的信息 姓名: ' + name + ' , 身份证: ' + idNo + ' , 手机号: ' + mobile)
    }
    //
    String accountId = args.containsKey('accountid') ? args.getString('accountid') : ''
    // init call parameters
    Map<String, Object> params = new HashMap() {
        {
            put('name', name)
            put('idNo', idNo)
            put('mobile', mobile)
            put('accountId', accountId)
        }
    }
    // call
    HttpResponse response = RestClient.doPostWithParams(gutsHubUrl, applySubmitPath, params, null)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '申请贷款没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            return ResponseVo.makeSuccess(message)
        }
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, '申请贷款失败')
    }
    // response status > 200
    return ResponseVo.makeFail(response.status, '申请贷款失败')
}

/**
 * 查询贷款进度
 * @param arguments 入参
 * @return JSONObject
 */
static def applyAuditStatus(String arguments) {
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    //
    String orderType = args.containsKey('orderType') ? args.getString('orderType') : ''
    // check
    if (StrUtil.isBlankIfStr(orderType)) {

        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供查询的订单类型')
    }
    String accountId = args.containsKey('accountid') ? args.getString('accountid') : ''
    //
    if ('提现' == orderType) {
        return ResponseVo.makeSuccess('正在放款中')
    } else if ('预审' == orderType) {
        // params init
        Map<String, Object> params = new HashMap() {
            {
                put('accountId', accountId)
            }
        }
        // call
        HttpResponse response = RestClient.doPostWithParams(gutsHubUrl, applyAuditStatusPath, params, null)
        // no response
        if (response == null) {
            return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '查询贷款进度没有响应')
        }
        // response status = 200
        if (response.isOk()) {
            JSONObject jsonObject = JSON.parseObject(response.body())
            if (jsonObject != null) {
                String message = jsonObject.getString('data')
                return ResponseVo.makeSuccess(message)
            }
            return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, '查询' + orderType + '进度失败，没有结果')
        }
        // response status > 200
        return ResponseVo.makeFail(response.status, '查询' + orderType + '进度失败')
    }
}

/**
 * 绑定银行卡
 * @param arguments 入参
 * @return JSONObject
 */
static def bindCard(String arguments) {
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    // check
    String cardNo = args.containsKey('cardNo') ? args.getString('cardNo') : ''
    String cardMobile = args.containsKey('cardMobile') ? args.getString('cardMobile') : ''
    if (StrUtil.isBlankIfStr(cardNo)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供银行卡号')
    }
    if (StrUtil.isBlankIfStr(cardMobile)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供银行卡预留手机号')
    }
    //
    if (!args.containsKey('confirm')) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '请仔细确认提供的信息 银行卡号: ' + cardNo + ' , 银行卡预留手机号: ' + cardMobile)
    }
    String accountId = args.containsKey('accountid') ? args.getString('accountId') : ''
    // params init
    Map<String, Object> params = new HashMap() {
        {
            put('accountId', accountId)
            put('cardNo', cardNo)
            put('reserveMobile', cardMobile)
        }
    }
    // call
    HttpResponse response = RestClient.doPostWithParams(gutsHubUrl, bindBankCardPath, params, null)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '绑定提现用的银行卡没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            return ResponseVo.makeSuccess(message)
        }
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, '绑定提现用的银行卡失败')
    }
    // response status > 200
    return ResponseVo.makeFail(response.status, '绑定提现用的银行卡失败')
}

/**
 * 绑定银行卡验证码
 * @param arguments 入参
 * @return JSONObject
 */
static def bindCardCaptcha(String arguments) {
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    //
    String verifyCode = args.containsKey('verifyCode') ? args.getString('verifyCode') : ''
    // check
    if (StrUtil.isBlankIfStr(verifyCode)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供验证码')
    }
    String accountId = args.containsKey('accountid') ? args.getString('accountid') : ''
    // params init
    Map<String, Object> params = new HashMap() {
        {
            put('accountId', accountId)
            put('verifyCode', verifyCode)
        }
    }
    // call
    HttpResponse response = RestClient.doPostWithParams(gutsHubUrl, bindBankCardCaptchaPath, params, null)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '验证银行卡验证码没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            return ResponseVo.makeSuccess(message)
        }
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, '验证银行卡验证码失败')
    }
    // response status > 200
    return ResponseVo.makeFail(response.status, '验证银行卡验证码失败')
}

/**
 * 放款
 * @param arguments 入参
 * @return JSONObject
 */
static def loanSubmit(String arguments) {
    // arguments init
    JSONObject args = JSON.parseObject(arguments)

    String strLoanAmount = args.containsKey('loanAmount') ? args.get('loanAmount').toString() : ''
    Integer period = args.containsKey('period') ? args.getInteger('period') : null
    // check
    if (StrUtil.isBlankIfStr(strLoanAmount)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供提现金额')
    }
    if (period == null) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供期数')
    }
    BigDecimal loanAmount = new BigDecimal(strLoanAmount)
    int amount = loanAmount.setScale(0, RoundingMode.UP).intValue()
    if (!args.containsKey('confirm')) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '请仔细确认 提现金额: ' + amount + ' ,期数: ' + period)
    }
    //
    String accountId = args.containsKey('accountid') ? args.getString('accountId') : ''
    // params init
    Map<String, Object> params = new HashMap() {
        {
            put('accountId', accountId)
            put('loanAmt', amount)
            put('period', period)
        }
    }
    // call
    HttpResponse response = RestClient.doPostWithParams(gutsHubUrl, loanSubmitPath, params, null)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '放款没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            return ResponseVo.makeSuccess(message)
        }
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, '放款失败')
    }
    // response status > 200
    return ResponseVo.makeFail(response.status, '放款失败')
}