package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.leapbound.yc.hub.entities.AccountEntity;
import com.github.leapbound.yc.hub.entities.RoleEntity;
import com.github.leapbound.yc.hub.entities.UserEntity;
import com.github.leapbound.yc.hub.mapper.AccountMapper;
import com.github.leapbound.yc.hub.mapper.RoleMapper;
import com.github.leapbound.yc.hub.mapper.UserMapper;
import com.github.leapbound.yc.hub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final RoleMapper roleMapper;

    @Override
    public String getUserByUsername(String username) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username)
                .last("limit 1");
        UserEntity userEntity = this.userMapper.selectOne(queryWrapper);
        if (userEntity != null) {
            return userEntity.getUserId();
        }
        return null;
    }

    @Override
    public String createUser(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCreateTime(new Date());
        userEntity.setUserId("U" + UUID.randomUUID().toString().replace("-", ""));
        this.userMapper.insert(userEntity);
        return userEntity.getUserId();
    }

    @Override
    public String getAccountId(String userId, String botId) {
        LambdaQueryWrapper<AccountEntity> queryWrapper = new LambdaQueryWrapper<AccountEntity>()
                .eq(AccountEntity::getUserId, userId)
                .eq(AccountEntity::getBotId, botId)
                .last("limit 1");
        AccountEntity accountEntity = this.accountMapper.selectOne(queryWrapper);
        if (accountEntity != null) {
            return accountEntity.getAccountId();
        }
        return null;
    }

    @Override
    public String createAccount(String accountName, String externalId, String userId, String botId) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setExternalId(externalId);
        accountEntity.setUserId(userId);
        accountEntity.setBotId(botId);
        accountEntity.setAccountId("A" + UUID.randomUUID().toString().replace("-", ""));
        accountEntity.setCreateTime(new Date());
        this.accountMapper.insert(accountEntity);
        return accountEntity.getAccountId();
    }

    @Override
    public void addAccountRoleRelation(String roleName, String accountId) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleName, roleName)
                .last("limit 1");
        RoleEntity roleEntity = this.roleMapper.selectOne(queryWrapper);
        if (roleEntity != null) {
            if (!this.roleMapper.checkRoleRelation(roleEntity.getRoleId(), accountId, 1)) {
                this.roleMapper.addRoleRelation(roleEntity.getRoleId(), accountId, 1);
            }
        }
    }
}
