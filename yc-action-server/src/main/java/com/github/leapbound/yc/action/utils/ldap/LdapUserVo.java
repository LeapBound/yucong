package com.github.leapbound.yc.action.utils.ldap;

/**
 * @author yamath
 * @since 2023/7/18 15:26
 */
public class LdapUserVo {

    private String error = "";

    private String accountName;

    private String commonName;

    private String distinguishedName;

    private String displayName;

    private String accountExpires;

    private String email;

    private String userAccountFlag;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccountExpires() {
        return accountExpires;
    }

    public void setAccountExpires(String accountExpires) {
        this.accountExpires = accountExpires;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserAccountFlag() {
        return userAccountFlag;
    }

    public void setUserAccountFlag(String userAccountFlag) {
        this.userAccountFlag = userAccountFlag;
    }

    @Override
    public String toString() {
        return "LdapUserVo{" +
                "error='" + error + '\'' +
                ", accountName='" + accountName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", distinguishedName='" + distinguishedName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", accountExpires='" + accountExpires + '\'' +
                ", email='" + email + '\'' +
                ", userAccountFlag='" + userAccountFlag + '\'' +
                '}';
    }
}
