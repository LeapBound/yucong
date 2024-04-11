package scripts.cashloan

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import groovy.transform.Field
import com.github.leapbound.yc.action.func.groovy.RestClient

import java.math.RoundingMode

/**
 *
 * @author yamath
 * @since 2023/10/16 13:27
 */
// geex-guts-hub 地址
@Field static String gutsHubUrl = 'https://beta.geexfinance.com/geex-guts-hub'
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
    if (arguments == null || arguments == '') {
        result.put('错误', '没有提供必要的信息')
        return result
    }
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
            result.put('结果', '没有执行方法')
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
    // result init
    JSONObject result = new JSONObject()
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    // check
    if (!args.containsKey('name') || !args.containsKey('idNo') || !args.containsKey('mobile')) {
        result.put('错误', '请提供 姓名，身份证，手机号，并仔细确认')
        return result
    }
    String name = args.getString('name')
    String idNo = args.getString('idNo')
    String mobile = args.getString('mobile')
    if (StrUtil.isBlankIfStr(name)) {
        result.put('错误', '请提供姓名')
        return result
    }
    if (StrUtil.isBlankIfStr(idNo)) {
        result.put('错误', '请提供身份证')
        return result
    }
    if (StrUtil.isBlankIfStr(mobile)) {
        result.put('错误', '请提供手机号')
        return result
    }
    // check confirm
    if (args.containsKey('confirm')) {
        result.put('消息', '请仔细确认提供的信息 姓名: ' + name + ' , 身份证: ' + idNo + ' , 手机号: ' + mobile)
        return result
    }
    //
    String accountId = args.getString('accountid')
    // init call parameters
    def params = ['name': name, 'idNo': idNo, 'mobile': mobile, 'accountId': accountId]
    // call
    def response = RestClient.doPostWithParams(gutsHubUrl, applySubmitPath, params, null)
    // no response
    if (response == null) {
        result.put('错误', '没有申请贷款的结果')
        return result
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            result.put('结果', message)
        } else {
            result.put('结果', '申请贷款失败')
        }
    } else { // response status > 200
        result.put('错误', response.status + ' 申请贷款失败')
    }
    return result
}

/**
 * 查询贷款进度
 * @param arguments 入参
 * @return JSONObject
 */
static def applyAuditStatus(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    // check
    if (!args.containsKey('orderType')) {
        result.put('消息', '需要查询哪种订单类型，预审提现')
        return result
    }
    String orderType = args.getString('orderType')
    if (StrUtil.isBlankIfStr(orderType)) {
        result.put('错误', '需要提供查询的订单类型')
        return result
    }
    String accountId = args.getString('accountid')
    //
    if ('提现' == orderType) {
        result.put('结果', '正在放款中')
    } else if ('预审' == orderType) {
        // params init
        def params = ['accountId': accountId]
        // call
        def response = RestClient.doPostWithParams(gutsHubUrl, applyAuditStatusPath, params, null)
        // no response
        if (response == null) {
            result.put('错误', '没有查询到贷款进度')
            return result
        }
        // response status = 200
        if (response.isOk()) {
            JSONObject jsonObject = JSON.parseObject(response.body())
            if (jsonObject != null) {
                String message = jsonObject.getString('data')
                result.put('结果', message)
            } else {
                result.put('结果', '查询' + orderType + '进度失败')
            }
        } else { // response status > 200
            result.put('结果', response.status + ' 查询' + orderType + '进度失败')
        }
    }
    return result
}

/**
 * 绑定银行卡
 * @param arguments 入参
 * @return JSONObject
 */
static def bindCard(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    // check
    if (!args.containsKey('cardNo') || !args.containsKey('cardMobile')) {
        result.put("错误", "请提供 银行卡号，银行卡预留手机号，并仔细确认")
        return result
    }
    String cardNo = args.getString('cardNo')
    String cardMobile = args.getString('cardMobile')
    if (StrUtil.isBlankIfStr(cardNo)) {
        result.put('错误', '请提供银行卡号')
        return result
    }
    if (StrUtil.isBlankIfStr(cardMobile)) {
        result.put('错误', '请提供银行卡预留手机号')
        return result
    }
    //
    if (!args.containsKey('confirm')) {
        result.put('消息', '请仔细确认提供的信息 银行卡号: ' + cardNo + ' , 银行卡预留手机号: ' + cardMobile)
        return result
    }
    String accountId = args.getString('accountId')
    // params init
    def params = ['accountId': accountId, 'cardNo': cardNo, 'reserveMobile': cardMobile]
    // call
    def response = RestClient.doPostWithParams(gutsHubUrl, bindBankCardPath, params, null)
    // no response
    if (response == null) {
        result.put('错误', '没有绑定提现用的银行卡结果')
        return result
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            result.put('结果', message)
        } else {
            result.put('结果', '绑定提现用的银行卡失败')
        }
    } else { // response status > 200
        result.put('结果', response.status + ' 绑定提现用的银行卡失败')
    }
    return result
}

/**
 * 绑定银行卡验证码
 * @param arguments 入参
 * @return JSONObject
 */
static def bindCardCaptcha(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    // check
    if (!args.containsKey('verifyCode')) {
        result.put('错误', '请提供验证码')
        return result
    }
    String verifyCode = args.getString('verifyCode')
    if (StrUtil.isBlankIfStr(verifyCode)) {
        result.put('错误', '请提供验证码')
        return result
    }
    String accountId = args.getString('accountId')
    // params init
    def params = ['accountId': accountId, 'verifyCode': verifyCode]
    // call
    def response = RestClient.doPostWithParams(gutsHubUrl, bindBankCardCaptchaPath, params, null)
    // no response
    if (response == null) {
        result.put('错误', '没有验证银行卡验证码结果')
        return result
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            result.put('结果', message)
        } else {
            result.put('结果', '验证银行卡验证码失败')
        }
    } else { // response status > 200
        result.put('结果', response.status + ' 验证银行卡验证码失败')
    }
    return result
}

/**
 * 放款
 * @param arguments 入参
 * @return JSONObject
 */
static def loanSubmit(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // arguments init
    JSONObject args = JSON.parseObject(arguments)
    // check
    if (!args.containsKey('loanAmount') || !args.containsKey('period')) {
        result.put('错误', '请提供 提现金额和期数')
        return result
    }
    if (StrUtil.isEmptyIfStr(args.get('loanAmount').toString())) {
        result.put('错误', '请提供提现金额')
        return result
    }
    Integer period = args.getInteger('period')
    if (period == null) {
        result.put('错误', '请提供期数')
        return result
    }
    BigDecimal loanAmount = new BigDecimal(args.get('loanAmount').toString())
    int amount = loanAmount.setScale(0, RoundingMode.UP).intValue()
    if (!args.containsKey('confirm')) {
        result.put('消息', '请仔细确认 提现金额: ' + amount + ' ,期数: ' + period)
        return result
    }
    //
    String accountId = args.getString('accountId')
    // params init
    def params = ['accountId': accountId, 'loanAmt': amount, 'period': period]
    // call
    def response = RestClient.doPostWithParams(gutsHubUrl, loanSubmitPath, params, null)
    // no response
    if (response == null) {
        result.put('错误', '没有放款结果')
        return result
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body())
        if (jsonObject != null) {
            String message = jsonObject.getString('data')
            result.put('结果', message)
        } else {
            result.put('结果', '放款失败')
        }
    } else { // response status > 200
        result.put('结果', response.status + ' 放款失败')
    }
    return result
}

// exec method
execCashLoanMethod(method, arguments)