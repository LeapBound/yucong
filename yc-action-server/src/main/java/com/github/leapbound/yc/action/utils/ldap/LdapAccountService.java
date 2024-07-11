package com.github.leapbound.yc.action.utils.ldap;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.action.model.vo.ResponseVo;

/**
 * @author yamath
 * @since 2023/7/18 11:25
 */
public interface LdapAccountService {

    /**
     * get ldap user info by account
     *
     * @param account user login account
     * @return ldap user info
     */
    ResponseVo<Object> getUserByAccount(String account);

    /**
     * get ldap user info by name
     *
     * @param name user name
     * @return ldap user info
     */
    ResponseVo<Object> getUserByName(String name);

    /**
     * disable ldap user account
     *
     * @param account user login account
     * @return result
     */
    ResponseVo<Object> closeLdapAccount(String account);

    /**
     * enable ldap user account
     *
     * @param account user login account
     * @return result
     */
    ResponseVo<Object> enableLdapAccount(String account);

    LdapUserVo closeLdapAccountByAccount(String account);
}
