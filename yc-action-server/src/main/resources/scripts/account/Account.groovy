package scripts.account

import cn.hutool.core.util.StrUtil
import cn.hutool.extra.spring.SpringUtil
import cn.hutool.http.HttpResponse
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.CommonMethod
import com.github.leapbound.yc.action.func.groovy.GeneralCodes
import com.github.leapbound.yc.action.func.groovy.ResponseVo
import com.github.leapbound.yc.action.utils.ldap.LdapAccountService
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicReference

/**
 *
 * @author yamath
 * @since 2023/10/13 17:25
 */

@Field static String qiguanUrl = ''
@Field static String getSalesListPath = '/geex-platform-web/management/user/getSalesList'
@Field static String getSalesDetailListPath = '/geex-platform-web/management/user/getSalesDetailList'
@Field static String updateSalesInfoPath = '/geex-platform-web/management/user/updateSalesInfo'
@Field static Logger logger = LoggerFactory.getLogger('scripts.account.Account');

// call method
execAccountMethod(method, arguments)
/**
 * 执行 account 相关方法
 * @param method function method
 * @param arguments 入参
 * @return JSONObject
 */
static def execAccountMethod(String method, String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // check
    if (StrUtil.isEmptyIfStr(arguments)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供必要的信息')
    }
    //
    Map<String, String> externalArgs = CommonMethod.getExternalArgs()
    qiguanUrl = externalArgs.get('qiguanUrl')
    //
    switch (method) {
        case 'close_user_account': // 关闭域账号和销售账号
            result = closeUserAccount(arguments)
            break
        case 'get_user_account': // 通过域账号信息获取 ldap 信息
            result = getUserByAccount(arguments)
            break
        case 'enable_user_account': // 激活 ldap 账号
            result = enableUserAccount(arguments)
            break
        case 'get_user_by_name': // 通过姓名获取 ldap 信息
            result = getUserByName(arguments)
            break
        default: // no method exist
            result = ResponseVo.makeFail(GeneralCodes.MISSING_EXEC_METHOD, '没有执行方法')
            break
    }
    return result
}

/**
 * 关闭域账号和销售账号
 * @param arguments 入参
 * @return JSONObject
 */
static def closeUserAccount(String arguments) {
    // result init
    JSONObject args = JSON.parseObject(arguments)
    // check account
    String account = args.containsKey('account') ? args.getString('account') : ''
    //
    if (StrUtil.isEmptyIfStr(account)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供域账号，要求用户提供域账号信息')
    }
    // ldapAccountService init
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // close ldap account
    JSONObject ldapUser = JSON.toJSON(ldapAccountService.closeLdapAccountByAccount(account)) as JSONObject
    if (ldapUser != null) {
        // check response
        if (ldapUser.containsKey('error') && !StrUtil.isEmptyIfStr(ldapUser.getString('error'))) {
            // error
            return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, ldapUser.getString('error'))
        } else {
            // success
            String resMsg = 'LDAP 用户账号已关闭。'
            // init
            String commonName = ldapUser.getString('commonName')
            // call close sales account
            Boolean response = closeSalesAccount(commonName, account)
            // no response
            if (response == null) {
                resMsg += "关闭销售操作无响应，联系管理员。"
                return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, resMsg)
            }
            // close status
            if (response) {
                resMsg += '销售账号已关闭。'
                return ResponseVo.makeSuccess(resMsg)
            } else {
                resMsg += '销售账号关闭失败。'
                return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_SERVER_FAILED, resMsg)
            }
        }
    }
    // ldap no response
    return ResponseVo.makeFail(GeneralCodes.REST_CALL_FAILED_NO_RESPONSE, 'LDAP 账号关闭异常')
}

/**
 * 通过域账号获取 ldap 信息
 * @param arguments 入参
 * @return JSONObject
 */
static def getUserByAccount(String arguments) {
    // result init
    JSONObject args = JSON.parseObject(arguments)
    // check account
    String account = args.containsKey('account') ? args.getString('account') : ''
    //
    if (StrUtil.isBlankIfStr(account)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供域账号，要求用户提供域账号信息')
    }
    // init ldapAccountService
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // response
    return ResponseVo.makeResponse(ldapAccountService.getUserByAccount(account))
}

/**
 * 激活域账号
 * @param arguments 入参
 * @return JSONObject
 */
static def enableUserAccount(String arguments) {
    // result init
    JSONObject args = JSON.parseObject(arguments)
    String account = args.containsKey('account') ? args.getString('account') : ''
    // check account
    if (StrUtil.isBlankIfStr(account)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供域账号，要求用户提供域账号信息')
    }
    // init ldapAccountService
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // response
    return ResponseVo.makeResponse(ldapAccountService.enableLdapAccount(account))
}

/**
 * 通过姓名获取 ldap 信息
 * @param arguments 入参
 * @return JSONObject
 */
static def getUserByName(String arguments) {
    // result init
    JSONObject args = JSON.parseObject(arguments)
    // check username
    String username = args.containsKey('username') ? args.getString('username') : ''
    //
    if (StrUtil.isBlankIfStr(username)) {
        return ResponseVo.makeFail(GeneralCodes.MISSING_REQUEST_PARAMS, '没有提供足够信息。要求用户提供姓名')
    }
    // init ldapAccountService
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // response
    return ResponseVo.makeResponse(ldapAccountService.getUserByName(username))
}

static def closeSalesAccount(String name, String ldapAccount) {
    //
    String personId = ldapAccount.toUpperCase().replace('GEEX', '')
    AtomicReference<Boolean> close = new AtomicReference<>(false)
    //
    Map<String, Object> params = ['userName': name, 'status': '1', 'page': '1', 'rows': 50] as Map<String, Object>
    Object[] methodArgs = [qiguanUrl, getSalesListPath, params, null, 1]
    HttpResponse response = CommonMethod.execCommonMethod('Alpha.groovy', 'doPostBodyWithLogin', methodArgs) as HttpResponse
    if (response == null) {
        logger.error('closeSalesAccount no response')
        return null
    }
    logger.info('closeSalesAccount response: {}', response.body())
    if (response.isOk()) {
        JSONArray rows = JSON.parseObject(response.body()).getJSONArray('rows')
        for (int i = 0; i < rows.size(); i++) {
            String userId = rows.getJSONObject(i).get('userId')
            Object[] methodArgs1 = [qiguanUrl, getSalesDetailListPath, ['userId': userId], null, 1]
            HttpResponse response1 = CommonMethod.execCommonMethod('Alpha.groovy', 'doGetWithLogin', methodArgs1) as HttpResponse
            if (response1 == null) {
                logger.warn('getSalesDetailList no response')
                continue
            }
            if (response1.isOk()) {
                JSONObject basicInfo = JSON.parseObject(response1.body()).getJSONObject('result').getJSONObject('basicInfo')
                if (basicInfo.getString('personId') == personId) {
                    logger.info('close sales account basicInfo: {}', basicInfo)
                    basicInfo.put('status', 0)
                    Map<String, Object> closeMap = ['basicInfo': basicInfo, 'rolesList': new ArrayList<>(0), 'teamList': new ArrayList<>(0)] as Map<String, Object>
                    Object[] methodArgs2 = [qiguanUrl, updateSalesInfoPath, closeMap, null, 1]
                    // Alpha.doPostBodyWithLogin
                    HttpResponse response2 = CommonMethod.execCommonMethod('Alpha.groovy', 'doPostBodyWithLogin', methodArgs2) as HttpResponse
                    if (response2 == null) {
                        logger.warn('updateSalesInfo no response')
                        continue
                    }
                    if (response2.isOk()) {
                        close.set(JSON.parseObject(response2.body()).getBooleanValue('success'))
                    }
                }
            }
        }
    }
    return close.get()
}