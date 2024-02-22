package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.leapbound.yc.hub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.github.leapbound.yc.hub.entities.AccountEntity;
import com.github.leapbound.yc.hub.entities.RoleEntity;
import com.github.leapbound.yc.hub.entities.UserEntity;
import com.github.leapbound.yc.hub.mapper.AccountMapper;
import com.github.leapbound.yc.hub.mapper.RoleMapper;
import com.github.leapbound.yc.hub.mapper.UserMapper;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final RoleMapper roleMapper;

    @Override
    public Long getUserByUsername(String username) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username)
                .last("limit 1");
        UserEntity userEntity = this.userMapper.selectOne(queryWrapper);
        if (userEntity != null) {
            return userEntity.getId();
        }
        return null;
    }

    @Override
    public Long createUser(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCreateTime(new Date());
        this.userMapper.insert(userEntity);
        return userEntity.getId();
    }

    @Override
    public Long getAccountNId(Long userId, Long botId) {
        LambdaQueryWrapper<AccountEntity> queryWrapper = new LambdaQueryWrapper<AccountEntity>()
                .eq(AccountEntity::getUserId, userId)
                .eq(AccountEntity::getBotId, botId)
                .last("limit 1");
        AccountEntity accountEntity = this.accountMapper.selectOne(queryWrapper);
        if (accountEntity != null) {
            return accountEntity.getId();
        }
        return null;
    }

    @Override
    public Long createAccount(String accountName, Long userNId, Long botUId) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountName(accountName);
        accountEntity.setUserId(userNId);
        accountEntity.setBotId(botUId);
        accountEntity.setCreateTime(new Date());
        this.accountMapper.insert(accountEntity);
        return accountEntity.getId();
    }

    @Override
    public void addAccountRoleRelation(String roleName, Long accountNId) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleName, roleName)
                .last("limit 1");
        RoleEntity roleEntity = this.roleMapper.selectOne(queryWrapper);
        if (roleEntity != null) {
            if (!this.roleMapper.checkRoleRelation(roleEntity.getId(), accountNId, 1)) {
                this.roleMapper.addRoleRelation(roleEntity.getId(), accountNId, 1);
            }
        }
    }
}
