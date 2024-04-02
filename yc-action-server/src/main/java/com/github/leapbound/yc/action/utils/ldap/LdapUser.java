package com.github.leapbound.yc.action.utils.ldap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

/**
 * @author yamath
 * @since 2023/7/18 9:37
 */
@Entry(objectClasses = {"user"})
public class LdapUser {

    @Id
    @JsonIgnore
    private Name dn;

    @Attribute(name = "sAMAccountName")
    private String accountName;

    @Attribute(name = "cn")
    private String commonName;

    @Attribute(name = "sn")
    private String surName;

    @Attribute(name = "givenname")
    private String givenName;

    @Attribute(name = "distinguishedName")
    private String distinguishedName;

    @Attribute(name = "displayName")
    private String displayName;

    @Attribute(name = "accountExpires")
    private String accountExpires;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "userAccountControl")
    private String userAccountControl;

    public Name getDn() {
        return dn;
    }

    public void setDn(Name dn) {
        this.dn = dn;
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

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
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

    public String getUserAccountControl() {
        return userAccountControl;
    }

    public void setUserAccountControl(String userAccountControl) {
        this.userAccountControl = userAccountControl;
    }

    @Override
    public String toString() {
        return "LdapUserVo{" +
                "dn=" + dn +
                ", accountName='" + accountName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", surName='" + surName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", distinguishedName='" + distinguishedName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", accountExpires='" + accountExpires + '\'' +
                ", email='" + email + '\'' +
                ", userAccountControl='" + userAccountControl + '\'' +
                '}';
    }
}
