package com.github.leapbound.yc.action.utils.ldap;

import com.alibaba.fastjson.JSONObject;

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
    JSONObject getUserByAccount(String account);

    /**
     * get ldap user info by name
     *
     * @param name user name
     * @return ldap user info
     */
    JSONObject getUserByName(String name);

    /**
     * disable ldap user account
     *
     * @param account user login account
     * @return result
     */
    JSONObject closeLdapAccount(String account);

    /**
     * enable ldap user account
     *
     * @param account user login account
     * @return result
     */
    JSONObject enableLdapAccount(String account);

    LdapUserVo closeLdapAccountByAccount(String account);
}
