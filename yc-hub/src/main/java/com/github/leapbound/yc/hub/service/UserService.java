package com.github.leapbound.yc.hub.service;

import com.github.leapbound.yc.hub.model.AccountDto;

public interface UserService {

    String getUserByUsername(String username);

    String createUser(String username);

    String getAccountId(String userId, String botId);

    AccountDto getAccountByChannelIdAndExternalId(String channelId, String externalId);

    String createAccount(String accountName, String externalId, String userId, String botId);

    void addAccountRoleRelation(String roleName, String accountId);
}
