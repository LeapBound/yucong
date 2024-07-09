package scripts.order

import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpResponse
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.RequestAuth
import com.github.leapbound.yc.action.func.groovy.ResponseVo
import groovy.transform.Field
import scripts.alpha.Alpha
import scripts.general.GeneralCodes
import scripts.general.GeneralMethods

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @author yamath
 * @since 2023/10/9 14:42
 *
 */
// alpha 地址
@Field static String alphaUrl = ''
// 通过订单号获取还款计划 yrl
@Field static String getRepayPlanByOrderPath = '/geex-csorder/order/repayPlan/'
// 查询订单放款状态 url
@Field static String getLoanMakeInfoByOrderPath = '/geex-fundOperate/fund/task/getApplyInfoList'
// 查询订单借据状态 url
@Field static String getLoanStatusByOrderPath = '/geex-core-web/loan/loanList'
// 还款试算 url
@Field static String tryOrderRepayPath = '/geex-platform-web/repayment/try/repay'
// 退款试算 url
@Field static String tryOrderRefundPath = '/geex-platform-web/repayment/try/refund'
// 放款状态 map
@Field static Map<String, String> loanOrderStatusMap = ['LOAN_WAIT' : '待放款(等待放款申请)',
                                                        'LOAN_ING'  : '放款中',
                                                        'LOAN_SUCC' : '放款成功',
                                                        'LOAN_FAIL' : '放款失败',
                                                        'AUDIT_PASS': '审批通过,待分配']

@Field static Map<String, Integer> loanCertificateStatusMap = ['取消'        : 0,
                                                               '正常'        : 1,
                                                               '退货预约'    : 2,
                                                               '退货完成'    : 3,
                                                               '提前还款预约': 4,
                                                               '提前还款完成': 5,
                                                               '逾期终止'    : 6,
                                                               '手动终止'    : 7,
                                                               '完成'        : 8]

execOrderMethod(method, arguments)

/**
 * exec 订单方法
 * @param method function method
 * @param arguments 入参
 * @return JSONObject
 */
static def execOrderMethod(String method, String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // check arguments
    if (StrUtil.isEmptyIfStr(arguments)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供必要的信息')
    }
    //
    alphaUrl = GeneralMethods.getExternal(arguments).get('alphaUrl')
    Alpha.alphaLoginUrl = alphaUrl
    //
    switch (method) {
        case 'get_user_repayment_by_order': // 订单号查询用户还款计划
            result = getUserRepaymentByOrder(arguments)
            break
        case 'get_user_loan_time_by_order': // 订单号查询用户放款时间
            result = getUserLoanTimeByOrder(arguments)
            break
        case 'get_loan_status_by_order': // 订单号查询用户借据状态
            result = getLoanStatusByOrder(arguments)
            break
        case 'try_order_repay': // 还款试算
            result = tryOrderRepay(arguments)
            break
        case 'try_order_refund': // 退货试算
            result = tryOrderRefund(arguments)
            break
        default: // no method exist
            result = ResponseVo.makeFail(GeneralCodes.MISSING_EXEC_METHOD, '没有执行方法')
            break
    }
    return result
}

/**
 * 订单号查询用户还款计划
 * @param arguments 入参
 * @return JSONObject
 */
static def getUserRepaymentByOrder(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    // check order no
    String orderNo = args.containsKey('orderNo') ? args.getString('orderNo') : ''
    if (StrUtil.isEmptyIfStr(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '执行参数没有 orderNo')
    }
    if (!matchOrderNo(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_PARAMS_INVALID, '订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号')
    }
    // call
    RequestAuth requestAuth = Alpha.setLoginRequestAuth()
    HttpResponse response = Alpha.doGetWithLogin(alphaUrl, getRepayPlanByOrderPath + orderNo, null, requestAuth, 1)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '查询用户的还款计划没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body()).getJSONObject('result')
        if (jsonObject != null) {
            // response data
            JSONArray array = jsonObject.getJSONArray('data')
            if (array != null && !array.isEmpty()) {
                int tenor = 0
                BigDecimal remain = new BigDecimal('0.0')
                String date = ''
                // 找到第一个未完成的期数和金额
                for (int i = 0; i < array.size(); i++) {
                    JSONObject replayPlan = array.getJSONObject(i)
                    if (replayPlan.containsKey('status')
                            && '正常' != replayPlan.getString('status')) {
                        continue
                    }
                    if (replayPlan.containsKey('remainAmount')
                            && BigDecimal.valueOf(0.0) == replayPlan.getBigDecimal('remainAmount')) {
                        continue
                    }
                    tenor = replayPlan.getIntValue('currTenor')
                    remain = replayPlan.getBigDecimal('remainAmount')
                    date = replayPlan.getString('payDate')
                    break
                }
                // 返回结果
                if (tenor > 0 && remain.compareTo(new BigDecimal('0.0')) > 0) {
                    return ResponseVo.makeSuccess(String.format('当前第 %d 期 %s 前还需还款 %.2f 元', tenor, date, remain))
                } else {
                    return ResponseVo.makeSuccess('已还清')
                }
            }
        }
        return ResponseVo.makeSuccess('没有查询到用户的还款计划')
    }
    return ResponseVo.makeFail(response.status, ' 查询用户的还款计划失败')
}

/**
 * 通过订单号查询放款时间
 * @param arguments
 * @return
 */
static def getUserLoanTimeByOrder(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    // check order no
    String orderNo = args.containsKey('orderNo') ? args.getString('orderNo') : ''
    if (StrUtil.isEmptyIfStr(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '执行参数没有 orderNo')
    }
    if (!matchOrderNo(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_PARAMS_INVALID, '订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号')
    }
    // params init
    Map<String, Object> params = new HashMap() {
        {
            put('orderNo', orderNo)
        }
    }
    // call
    RequestAuth requestAuth = Alpha.setLoginRequestAuth()
    HttpResponse response = Alpha.doPostBodyWithLogin(alphaUrl, getLoanMakeInfoByOrderPath, params, requestAuth, 1)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '查询订单的放款信息没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body()).getJSONArray('rows')[0]
        if (jsonObject != null) {
            JSONObject loanInfo = jsonObject.getJSONObject('data')
            if (loanInfo != null) {
                // check order status and loan time
                if (loanInfo.containsKey('frozenStatus') && loanInfo.getIntValue('frozenStatus') > 0) {
                    return ResponseVo.makeSuccess('订单已冻结')
                }
                //
                if (!loanInfo.containsKey('orderStatus')
                        || (loanInfo.containsKey('loanTime')
                        && (null == loanInfo.getString('loanTime') || '' == loanInfo.getString('loanTime')))) {
                    return ResponseVo.makeSuccess('订单没有放款状态')
                }
                // init
                String statusDesc = loanOrderStatusMap.get(loanInfo.getString('orderStatus'))
                String loanTime = loanInfo.getString('loanTime')
                String loanApplyTime = loanInfo.getString('loanApplyTime')
                String message = loanInfo.getString('message')
                String rs = ' 放款状态： ' + statusDesc
                if (null == loanTime || '' == loanTime) {
                    if (null != loanApplyTime && '' != loanApplyTime) {
                        rs += ' 申请时间：' + loanApplyTime
                    }
                } else {
                    rs += ' 放款时间：' + loanTime
                }
                if (null != message && '' != message) {
                    rs += ' 消息：' + message
                }
                return ResponseVo.makeSuccess(rs)
            }
        }
        return ResponseVo.makeSuccess('没有查询到订单的放款信息')
    }
    return ResponseVo.makeFail(response.status, ' 查询订单的放款信息失败')
}

/**
 * 订单号查询借据状态
 * @param arguments 入参
 * @return JSONObject
 */
static def getLoanStatusByOrder(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    // check order no
    String orderNo = args.containsKey('orderNo') ? args.getString('args') : ''
    if (StrUtil.isEmptyIfStr(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '执行参数没有 orderNo')
    }
    if (!matchOrderNo(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_PARAMS_INVALID, '订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号')
    }
    // params init
    Map<String, Object> params = new HashMap() {
        {
            put('orderNo', orderNo)
        }
    }
    // call
    RequestAuth requestAuth = Alpha.setLoginRequestAuth()
    HttpResponse response = Alpha.doPostBodyWithLogin(alphaUrl, getLoanStatusByOrderPath, params, requestAuth, 1)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '查询订单借据状态信息没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body()).getJSONArray('rows')[0]
        if (jsonObject != null) {
            JSONObject loanStatus = jsonObject.getJSONObject('data')
            if (loanStatus != null) {
                // loan status
                String strLoanStatus = loanStatus.getString('loanStatus')
                String message = ' 借据状态：' + strLoanStatus
                if ('退货完成' == strLoanStatus
                        || '提前还款完成' == strLoanStatus
                        || '完成' == strLoanStatus) {
                    message += ' 订单已结清'
                } else {
                    message += ' 订单未结清'
                }
                return ResponseVo.makeSuccess(message)
            }
        }
        return ResponseVo.makeSuccess('没有查询到订单借据状态信息')
    }
    return ResponseVo.makeFail(response.status, ' 查询订单借据状态信息失败')
}

/**
 * 还款试算
 * @param arguments 入参
 * @return JSONObject
 */
static def tryOrderRepay(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    // check order no
    String orderNo = args.containsKey('orderNo') ? args.getString('orderNo') : ''
    if (StrUtil.isEmptyIfStr(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '执行参数没有 orderNo')
    }
    if (!matchOrderNo(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_PARAMS_INVALID, '订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号')
    }
    // params init
    Map<String, Object> params = new HashMap() {
        {
            put('orderNo', orderNo)
        }
    }
    // call
    RequestAuth requestAuth = Alpha.setLoginRequestAuth()
    HttpResponse response = Alpha.doPostBodyWithLogin(alphaUrl, tryOrderRepayPath, params, requestAuth, 1)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '获取还款试算结果没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body()).getJSONObject('result')
        if (jsonObject != null) {
            JSONObject tryOrder = jsonObject.getJSONObject('data')
            if (tryOrder != null) {
                // amount
                BigDecimal amount = tryOrder.getBigDecimal('amount')
                String payDate = tryOrder.getString('payDate')
                DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss')
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern('yyyy年MM月dd日')
                String message = amount + " 截止" + LocalDateTime.parse(payDate, parseFormatter).format(dateTimeFormatter)
                //
                return ResponseVo.makeSuccess(message)
            }
        }
        return ResponseVo.makeSuccess('没有获取到还款试算结果')

    }
    return ResponseVo.makeFail(response.status, ' 获取还款试算结果失败')
}

/**
 * 退款试算
 * @param arguments 入参
 * @return JSONObject
 */
static def tryOrderRefund(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    // check order no
    String orderNo = args.containsKey('orderNo') ? args.getString('orderNo') : ''
    if (StrUtil.isEmptyIfStr(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '执行参数没有 orderNo')
    }
    if (!matchOrderNo(orderNo)) {
        return ResponseVo.makeFail(GeneralCodes.LOGIC_FAILED_PARAMS_INVALID, '订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号')
    }
    // params init
    Map<String, Object> params = new HashMap() {
        {
            put('orderNo', orderNo)
        }
    }
    // call
    RequestAuth requestAuth = Alpha.setLoginRequestAuth()
    HttpResponse response = Alpha.doPostBodyWithLogin(alphaUrl, tryOrderRefundPath, params, requestAuth, 1)
    // no response
    if (response == null) {
        return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, '获取退款试算结果没有响应')
    }
    // response status = 200
    if (response.isOk()) {
        JSONObject jsonObject = JSON.parseObject(response.body()).getJSONObject('result')
        if (jsonObject != null) {
            JSONObject tryOrder = jsonObject.getJSONObject('data')
            if (tryOrder != null) {
                // amount
                BigDecimal amount = tryOrder.getBigDecimal('amount')
                String payDate = tryOrder.getString('payDate')
                DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss')
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern('yyyy年MM月dd日')
                String message = amount + " 截止" + LocalDateTime.parse(payDate, parseFormatter).format(dateTimeFormatter)
                return ResponseVo.makeSuccess(message)
            }
        }
        return ResponseVo.makeSuccess('没有获取到退款试算结果')
    }
    return ResponseVo.makeFail(response.status, ' 获取退款试算结果失败')
}

static def matchOrderNo(orderNo) {
    String reg = /^[A-Z]{3}[0-9]{2}-[0-9]{6}-[0-9]{6}$/
    if (orderNo.matches(reg)) {
        return true
    }
    return false
}
