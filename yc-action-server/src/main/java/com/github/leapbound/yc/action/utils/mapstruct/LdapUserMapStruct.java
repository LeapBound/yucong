package com.github.leapbound.yc.action.utils.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.github.leapbound.yc.action.utils.ldap.LdapUser;
import com.github.leapbound.yc.action.utils.ldap.LdapUserVo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yamath
 * @since 2023/7/18 15:29
 */
@Mapper
public interface LdapUserMapStruct {

    LdapUserMapStruct INSTANCE = Mappers.getMapper(LdapUserMapStruct.class);

    static final String[] DISABLED_USER_ACCOUNT_CONTROL_FLAG = {"514", "546", "66050", "66080", "66082"};
    static final String[] ENABLED_USER_ACCOUNT_CONTROL_FLAG = {"512", "544", "66048", "262656"};

    @Mapping(target = "error", ignore = true)
    @Mapping(target = "accountName", expression = "java(ldapUser.getAccountName())")
    @Mapping(target = "commonName", expression = "java(ldapUser.getCommonName())")
    @Mapping(target = "distinguishedName", expression = "java(ldapUser.getDistinguishedName())")
    @Mapping(target = "displayName", expression = "java(ldapUser.getDisplayName())")
    @Mapping(target = "accountExpires", expression = "java(LdapUserMapStruct.accountExpiresToDate( ldapUser.getAccountExpires()))")
    @Mapping(target = "email", expression = "java(ldapUser.getEmail())")
    @Mapping(target = "userAccountFlag", expression = "java(LdapUserMapStruct.checkUserAccountControl(ldapUser.getUserAccountControl()))")
    LdapUserVo ldapUserToVo(LdapUser ldapUser);

    static String checkUserAccountControl(String userAccountControl) {
        if (Arrays.asList(DISABLED_USER_ACCOUNT_CONTROL_FLAG).contains(userAccountControl)) {
            return "账号已禁用";
        } else if (Arrays.asList(ENABLED_USER_ACCOUNT_CONTROL_FLAG).contains(userAccountControl)) {
            return "账号启用";
        }
        return "";
    }

    static String accountExpiresToDate(String accountExpires) {
        if ("0".equals(accountExpires)) {
            return "永不过期";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1601, 0, 1, 0, 0);
        Date expires = new Date(Long.parseLong(accountExpires) / 10000 + calendar.getTime().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(expires);
    }
}
