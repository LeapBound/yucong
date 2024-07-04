package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.leapbound.yc.hub.entities.AccountEntity;
import com.github.leapbound.yc.hub.entities.ChannelEntity;
import com.github.leapbound.yc.hub.mapper.AccountMapper;
import com.github.leapbound.yc.hub.mapper.ChannelMapper;
import com.github.leapbound.yc.hub.model.ChannelDto;
import com.github.leapbound.yc.hub.service.ChannelService;
import com.github.leapbound.yc.hub.utils.bean.ChannelBeanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelMapper channelMapper;
    private final AccountMapper accountMapper;

    @Override
    public ChannelDto getChannelByAccountId(String accountId) {
        ChannelEntity channelEntity = getChannelEntityByAccountId(accountId);
        return ChannelBeanMapper.mapEntityToModel(channelEntity);
    }

    @Override
    public ChannelDto getChannelByOpenKfId(String openKfId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getOpenKfId, openKfId)
                .last("limit 1");
        ChannelEntity channelEntity = this.channelMapper.selectOne(queryWrapper);

        return ChannelBeanMapper.mapEntityToModel(channelEntity);
    }

    @Override
    public ChannelDto getChannelByCorpIdAndAgentId(String corpId, String agentId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getCorpId, corpId)
                .eq(ChannelEntity::getAgentId, agentId)
                .last("limit 1");
        ChannelEntity channelEntity = this.channelMapper.selectOne(queryWrapper);

        return ChannelBeanMapper.mapEntityToModel(channelEntity);
    }

    private ChannelEntity getChannelEntityByAccountId(String accountId) {
        LambdaQueryWrapper<AccountEntity> accountQueryWrapper = new LambdaQueryWrapper<AccountEntity>()
                .eq(AccountEntity::getAccountId, accountId)
                .last("limit 1");
        AccountEntity accountEntity = this.accountMapper.selectOne(accountQueryWrapper);

        LambdaQueryWrapper<ChannelEntity> channelQueryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getChannelId, accountEntity.getChannelId())
                .last("limit 1");
        return this.channelMapper.selectOne(channelQueryWrapper);
    }

}
