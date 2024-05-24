package com.github.leapbound.yc.hub.service;

public interface UserService {

    String getUserByUsername(String username);

    String createUser(String username);

    String getAccountId(String userId, String botId);

    String createAccount(String accountName, String externalId, String userId, String botId);

    void addAccountRoleRelation(String roleName, String accountId);
}
