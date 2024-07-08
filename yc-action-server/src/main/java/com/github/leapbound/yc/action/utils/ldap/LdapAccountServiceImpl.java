package com.github.leapbound.yc.action.utils.ldap;

import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.utils.mapstruct.LdapUserMapStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

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


    @Override
    public ResponseVo<Object> getUserByAccount(String account) {
        List<LdapUser> list = this.getLdapUserByAccount(account);
        if (list == null || list.isEmpty()) {
            logger.warn("no user found, account = {}", account);
            return ResponseVo.fail(3204, "没有找到账号信息");
        }
        if (list.size() > 1) {
            logger.warn("multi user found, account = {}", account);
            return ResponseVo.fail(3204, "找到多个账号", list);
        }
        LdapUser vo = list.get(0);
        return ResponseVo.success(LdapUserMapStruct.INSTANCE.ldapUserToVo(vo));
    }

    @Override
    public ResponseVo<Object> getUserByName(String name) {
        List<LdapUser> list = this.getLdapUserByName(name);
        // no user
        if (list == null || list.isEmpty()) {
            logger.warn("no user found, name = {}", name);
            return ResponseVo.fail(3204, "没有找到账号信息");
        }
        // multi account
        if (list.size() > 1) {
            logger.warn("multi user found, name = {}", name);
            return ResponseVo.fail(3204, "找到多个账号", list);
        }
        LdapUser vo = list.get(0);
        return ResponseVo.success(LdapUserMapStruct.INSTANCE.ldapUserToVo(vo));
    }

    public ResponseVo<Object> closeLdapAccount(String account) {
        try {
            List<LdapUser> list = this.getLdapUserByAccount(account);
            // no user
            if (list == null || list.isEmpty()) {
                logger.warn("no user found, account = {}", account);
                return ResponseVo.fail(3204, "没有找到账号");
            }
            // multi account
            if (list.size() > 1) {
                logger.warn("multi user found, account = {}", account);
                return ResponseVo.fail(3204, "找到多个账号", list);
            }
            // find account
            LdapUser vo = list.get(0);
            // disable account, 512 + 2 = 514
            this.modifyUserAttribute(vo, NORMAL_ACCOUNT + ACCOUNT_DISABLE);
            //
            return ResponseVo.success("成功关闭账号");
        } catch (Exception ex) {
            logger.error("close ldap account error", ex);
            return ResponseVo.fail(3204, "关闭账号时发生异常");
        }
    }

    public ResponseVo<Object> enableLdapAccount(String account) {
        try {
            List<LdapUser> list = this.getLdapUserByAccount(account);
            // no user
            if (list == null || list.isEmpty()) {
                logger.warn("no user found, account = {}", account);
                return ResponseVo.fail(3204, "没有找到账号");
            }
            // multi account
            if (list.size() > 1) {
                return ResponseVo.fail(3204, "找到多个账号", list);
            }
            //
            LdapUser vo = list.get(0);
            // enable account
            this.modifyUserAttribute(vo, NORMAL_ACCOUNT + DONT_EXPIRE_PASSWORD);
            //
            return ResponseVo.success("成功重新启用账号");
        } catch (Exception ex) {
            logger.error("enable ldap account error", ex);
            return ResponseVo.fail(3204, "启用账号时发生异常");
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
