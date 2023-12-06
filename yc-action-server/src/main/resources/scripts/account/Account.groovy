package scripts.account

import cn.hutool.core.util.StrUtil
import cn.hutool.extra.spring.SpringUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import groovy.transform.Field
import yzggy.yucong.action.func.groovy.RestClient
import yzggy.yucong.action.utils.ldap.LdapAccountService

/**
 *
 * @author yamath
 * @since 2023/10/13 17:25
 */
// geex-guts-hub 地址
@Field static String gutsHubUrl = 'https://beta.geexfinance.com/geex-guts-hub'
// 关闭销售账号 url
@Field static String closeSalesAccountPath = '/alpha/bd/account/close'

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
            def params = ['name': commonName, 'ldap': account]
            // call close sales account
            def response = RestClient.doPostWithParams(gutsHubUrl, closeSalesAccountPath, params, null)
            // no response
            if (response == null) {
                result.put('销售账号操作错误', '没有关闭销售账号结果')
                return result
            }
            // response status = 200
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body())
                if (jsonObject != null) {
                    if (jsonObject.containsKey('status')) {
                        // close status
                        if (jsonObject.getBooleanValue('status')) {
                            result.put('销售账号操作结果', '销售账号已关闭')
                        } else {
                            result.put('销售账号操作结果', '销售账号关闭失败')
                        }
                    } else { // response no data
                        result.put('销售账号操作结果', '销售账号关闭失败')
                    }
                } else { // no response
                    result.put('销售账号操作结果', '没有关闭销售账号结果')
                }
            } else { // response status > 200
                result.put('销售账号操作结果', response.status + ' 销售账号关闭失败')
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

// call method
execAccountMethod(method, arguments)