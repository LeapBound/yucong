package com.github.leapbound.yc.action.utils.ldap;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import com.github.leapbound.yc.action.utils.mapstruct.LdapUserMapStruct;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import java.util.List;

/**
 * @author yamath
 * @since 2023/7/17 14:26
 */
@Service
public class LdapAccountServiceImpl implements LdapAccountService {

    private static final Logger logger = LoggerFactory.getLogger(LdapAccountServiceImpl.class);

    private static final int ACCOUNT_DISABLE = 2;
    private static final int NORMAL_ACCOUNT = 512;
    private static final int DONT_EXPIRE_PASSWORD = 65536;

    private final LdapTemplate ldapTemplate;

    public LdapAccountServiceImpl(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public JSONObject getUserByAccount(String account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        List<LdapUser> list = this.getLdapUserByAccount(account);
        // no user
        if (list == null || list.isEmpty()) {
            logger.warn("no user found, account = {}", account);
            jsonObject.put("执行结果", "没有找到账号信息");
            return jsonObject;
        }
        // multi account
        if (list.size() > 1) {
            logger.warn("multi user found, account = {}", account);
            jsonObject.put("执行结果", "找到多个账号");
            jsonObject.put("账号", list);
            return jsonObject;
        }
        LdapUser vo = list.get(0);
        logger.debug("ldap user, {}", vo);
        jsonObject.put("账号信息", LdapUserMapStruct.INSTANCE.ldapUserToVo(vo));
        return jsonObject;
    }

    @Override
    public JSONObject getUserByName(String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", name);
        List<LdapUser> list = this.getLdapUserByName(name);
        // no user
        if (list == null || list.isEmpty()) {
            logger.warn("no user found, name = {}", name);
            jsonObject.put("执行结果", "没有找到账号信息");
            return jsonObject;
        }
        // multi account
        if (list.size() > 1) {
            logger.warn("multi user found, name = {}", name);
            jsonObject.put("执行结果", "找到多个账号");
            jsonObject.put("账号", list);
            return jsonObject;
        }
        LdapUser vo = list.get(0);
        logger.debug("ldap user, {}", vo);
        jsonObject.put("账号信息", LdapUserMapStruct.INSTANCE.ldapUserToVo(vo));
        return jsonObject;
    }

    public JSONObject closeLdapAccount(String account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        try {
            List<LdapUser> list = this.getLdapUserByAccount(account);
            // no user
            if (list == null || list.isEmpty()) {
                logger.warn("no user found, account = {}", account);
                jsonObject.put("执行结果", "没有找到账号");
                return jsonObject;
            }
            // multi account
            if (list.size() > 1) {
                jsonObject.put("执行结果", "找到多个账号，请确认");
                jsonObject.put("账号", list);
                return jsonObject;
            }
            // find account
            LdapUser vo = list.get(0);
            // disable account, 512 + 2 = 514
            this.modifyUserAttribute(vo, NORMAL_ACCOUNT + ACCOUNT_DISABLE);
            //
            jsonObject.put("执行成功", "成功关闭账号");
            return jsonObject;
        } catch (Exception ex) {
            logger.error("close ldap account error", ex);
            jsonObject.put("执行异常", "关闭账号时发生异常");
            return jsonObject;
        }
    }

    public JSONObject enableLdapAccount(String account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        try {
            List<LdapUser> list = this.getLdapUserByAccount(account);
            // no user
            if (list == null || list.isEmpty()) {
                logger.warn("no user found, account = {}", account);
                jsonObject.put("执行结果", "没有找到账号");
                return jsonObject;
            }
            // multi account
            if (list.size() > 1) {
                jsonObject.put("执行结果", "找到多个账号，请确认");
                jsonObject.put("账号", list);
                return jsonObject;
            }
            //
            LdapUser vo = list.get(0);
            // enable account
            this.modifyUserAttribute(vo, NORMAL_ACCOUNT + DONT_EXPIRE_PASSWORD);
            //
            jsonObject.put("执行成功", "成功重新启用账号");
            return jsonObject;
        } catch (Exception ex) {
            logger.error("close ldap account error", ex);
            jsonObject.put("执行异常", "启用账号时发生异常");
            return jsonObject;
        }
    }

    @Override
    public LdapUserVo closeLdapAccountByAccount(String account) {
        LdapUserVo ldapUserVo = new LdapUserVo();
        try {
            List<LdapUser> list = this.getLdapUserByAccount(account);
            // no user
            if (list == null || list.isEmpty()) {
                logger.warn("no user found, account = {}", account);
                ldapUserVo.setError(String.format("没有找到 LDAP 用户, %s", account));
                return ldapUserVo;
            }
            // multi account
            if (list.size() > 1) {
                logger.warn("multi user found, account = {}", account);
                ldapUserVo.setError(String.format("找到多个 LDAP 用户, %s", account));
                return ldapUserVo;
            }
            // find account
            LdapUser vo = list.get(0);
            // disable account, 512 + 2 = 514
            this.modifyUserAttribute(vo, NORMAL_ACCOUNT + ACCOUNT_DISABLE);
            //
            return LdapUserMapStruct.INSTANCE.ldapUserToVo(vo);
        } catch (Exception ex) {
            logger.error("close ldap account error", ex);
            ldapUserVo.setError("关闭 LDAP 用户账号异常");
            return ldapUserVo;
        }
    }

    /**
     * get ldap user info by account
     *
     * @param account user login account
     * @return ldap user info
     */
    private List<LdapUser> getLdapUserByAccount(String account) {
        try {
            LdapQuery query = LdapQueryBuilder.query().where("sAMAccountName").is(account);
            return this.ldapTemplate.find(query, LdapUser.class);
        } catch (Exception ex) {
            logger.error("get ldap user by account error, account = {}", account, ex);
            return null;
        }
    }

    /**
     * get ldap user info by name
     *
     * @param name user name
     * @return ldap user info
     */
    private List<LdapUser> getLdapUserByName(String name) {
        try {
            LdapQuery query = LdapQueryBuilder.query().where("cn").is(name);
            return this.ldapTemplate.find(query, LdapUser.class);
        } catch (Exception ex) {
            logger.error("get ldap user by name error, name = {}", name, ex);
            return null;
        }
    }

    /**
     * modify ldap user attribute
     *
     * @param vo            ldap user info
     * @param attributeFlag userAccountControl
     * @throws Exception ex
     */
    private void modifyUserAttribute(LdapUser vo, int attributeFlag) throws Exception {
        try {
            // ldap dn
            LdapName ldapName = LdapNameBuilder.newInstance(vo.getDn()).build();
            // modify attribute
            ldapTemplate.modifyAttributes(ldapName, new ModificationItem[]{
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl", String.valueOf(attributeFlag)))
            });
        } catch (Exception ex) {
            logger.error("modify ldap user attribute error, account = {}, flag = {}", vo.getAccountName(), attributeFlag);
            throw new Exception(ex);
        }
    }
}
