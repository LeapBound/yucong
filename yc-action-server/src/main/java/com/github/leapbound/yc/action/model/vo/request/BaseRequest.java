package com.github.leapbound.yc.action.model.vo.request;

import java.io.Serializable;

/**
 * @author yamath
 * @since 2023/7/3 16:13
 */
public class BaseRequest implements Serializable {

    private String userName;

    private String accountId;
    private String deviceId;

    private int rows;

    private int page;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "userName='" + userName + '\'' +
                ", accountId='" + accountId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", rows=" + rows +
                ", page=" + page +
                '}';
    }
}
