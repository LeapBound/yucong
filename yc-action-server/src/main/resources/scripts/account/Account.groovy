package scripts.account

import cn.hutool.core.util.StrUtil
import cn.hutool.extra.spring.SpringUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.RequestAuth
import com.github.leapbound.yc.action.utils.ldap.LdapAccountService
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import scripts.alpha.Alpha

import java.util.concurrent.atomic.AtomicReference

/**
 *
 * @author yamath
 * @since 2023/10/13 17:25
 */

@Field static String alphaUrl = 'https://beta.geexfinance.com'
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
    if (arguments == null || arguments == '') {
        result.put('错误', '没有提供必要的信息')
        return result
    }
    //
    def externalUrl = getExternalUrl(arguments)
    if (!StrUtil.isEmptyIfStr(externalUrl)) {
        alphaUrl = externalUrl
    }
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

/**
 * 关闭域账号和销售账号
 * @param arguments 入参
 * @return JSONObject
 */
static def closeUserAccount(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // check account
    if (!JSON.parseObject(arguments).containsKey('account')) {
        result.put('错误', '没有提供必要的 account')
        return result
    }
    String account = JSON.parseObject(arguments).getString('account')
    //
    if (StrUtil.isBlankIfStr(account)) {
        result.put('错误', '没有提供足够信息。要求用户提供域账号信息')
        return result
    }
    // ldapAccountService init
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // close ldap account
    JSONObject ldapUser = JSON.toJSON(ldapAccountService.closeLdapAccountByAccount(account)) as JSONObject
    if (ldapUser != null) {
        // check response
        if (ldapUser.containsKey('error') && !StrUtil.isBlankIfStr(ldapUser.getString('error'))) {
            // error
            result.put('LDAP操作结果', ldapUser.getString('error'))
        } else {
            // success
            result.put('LDAP操作结果', 'LDAP 用户账号已关闭')
            // init
            String commonName = ldapUser.getString('commonName')
            // call close sales account
            def response = closeSalesAccount(commonName, account)
            // no response
            if (response == null) {
                result.put('销售账号操作错误', '操作无响应，联系管理员')
                return result
            }
            // close status
            if (response) {
                result.put('销售账号操作结果', '销售账号已关闭')
            } else {
                result.put('销售账号操作结果', '销售账号关闭失败')
            }
        }
    } else { // ldap no response
        result.put('LDAP操作结果', 'LDAP 账号关闭异常')
    }
    return result
}

/**
 * 通过域账号获取 ldap 信息
 * @param arguments 入参
 * @return JSONObject
 */
static def getUserByAccount(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // check account
    if (!JSON.parseObject(arguments).containsKey('account')) {
        result.put('错误', '没有提供必要的 account')
        return result
    }
    String account = JSON.parseObject(arguments).getString('account')
    //
    if (StrUtil.isBlankIfStr(account)) {
        result.put('错误', '没有提供足够信息。要求用户提供域账号信息')
        return result
    }
    // init ldapAccountService
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // response
    result = ldapAccountService.getUserByAccount(account)
    return result
}

/**
 * 激活域账号
 * @param arguments 入参
 * @return JSONObject
 */
static def enableUserAccount(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // check account
    if (!JSON.parseObject(arguments).containsKey('account')) {
        result.put('错误', '没有提供必要的 account')
        return result
    }
    String account = JSON.parseObject(arguments).getString('account')
    //
    if (StrUtil.isBlankIfStr(account)) {
        result.put('错误', '没有提供足够信息。要求用户提供域账号信息')
        return result
    }
    // init ldapAccountService
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // response
    result = ldapAccountService.enableLdapAccount(account)
    return result
}

/**
 * 通过姓名获取 ldap 信息
 * @param arguments 入参
 * @return JSONObject
 */
static def getUserByName(String arguments) {
    // result init
    JSONObject result = new JSONObject()
    // check username
    if (!JSON.parseObject(arguments).containsKey('username')) {
        result.put('错误', '没有提供必要的 account')
        return result
    }
    String username = JSON.parseObject(arguments).getString('username')
    //
    if (StrUtil.isBlankIfStr(username)) {
        result.put('错误', '没有提供足够信息。要求用户提供姓名')
        return result
    }
    // init ldapAccountService
    LdapAccountService ldapAccountService = SpringUtil.getBean(LdapAccountService.class)
    // response
    result = ldapAccountService.getUserByName(username)
    return result
}

static def closeSalesAccount(String name, String ldapAccount) {
    //
    String personId = ldapAccount.toUpperCase().replace('GEEX', '')
    AtomicReference<Boolean> close = new AtomicReference<>(false)
    //
    def params = ['userName': name, 'status': '1', 'page': '1', 'rows': 50]
    RequestAuth requestAuth = Alpha.setLoginRequestAuth()
    def response = Alpha.doPostBodyWithLogin(alphaUrl, getSalesListPath, params, requestAuth, 1)
    if (response == null) {
        logger.error('closeSalesAccount no response')
        return null
    }
    logger.info('closeSalesAccount response: {}', response.body())
    if (response.isOk()) {
        JSONArray rows = JSON.parseObject(response.body()).getJSONArray('rows')
        for (int i = 0; i < rows.size(); i++) {
            String userId = rows.getJSONObject(i).get('userId')
            requestAuth = Alpha.setLoginRequestAuth()
            def response1 = Alpha.doGetWithLogin(alphaUrl, getSalesDetailListPath, ['userId': userId], requestAuth, 1)
            if (response1 == null) {
                logger.warn('getSalesDetailList no response')
                continue
            }
            if (response1.isOk()) {
                JSONObject basicInfo = JSON.parseObject(response1.body()).getJSONObject('result').getJSONObject('basicInfo')
                if (basicInfo.getString('personId') == personId) {
                    logger.info('close sales account basicInfo: {}', basicInfo)
                    basicInfo.put('status', 0)
                    def closeMap = ['basicInfo': basicInfo, 'rolesList': new ArrayList<>(0), 'teamList': new ArrayList<>(0)]
                    requestAuth = Alpha.setLoginRequestAuth()
                    def response2 = Alpha.doPostBodyWithLogin(alphaUrl, updateSalesInfoPath, closeMap, requestAuth, 1)
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