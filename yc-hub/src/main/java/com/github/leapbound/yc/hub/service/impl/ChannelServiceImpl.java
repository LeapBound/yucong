package com.github.leapbound.yc.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.leapbound.yc.hub.entities.AccountEntity;
import com.github.leapbound.yc.hub.entities.ChannelEntity;
import com.github.leapbound.yc.hub.entities.WxKfEntity;
import com.github.leapbound.yc.hub.handler.wx.cp.CpKfHandler;
import com.github.leapbound.yc.hub.handler.wx.cp.CpLogHandler;
import com.github.leapbound.yc.hub.handler.wx.cp.CpMsgHandler;
import com.github.leapbound.yc.hub.handler.wx.mp.MpLogHandler;
import com.github.leapbound.yc.hub.handler.wx.mp.MpMsgHandler;
import com.github.leapbound.yc.hub.mapper.AccountMapper;
import com.github.leapbound.yc.hub.mapper.ChannelMapper;
import com.github.leapbound.yc.hub.mapper.WxCpKfMapper;
import com.github.leapbound.yc.hub.model.wx.WxCpKfDto;
import com.github.leapbound.yc.hub.service.ChannelService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpKfServiceImpl;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.kf.WxCpKfServicerListResp;
import me.chanjar.weixin.cp.bean.kf.WxCpKfServicerOpResp;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.cp.constant.WxCpConsts;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final CpLogHandler cpLogHandler;
    private final CpMsgHandler cpMsgHandler;
    private final CpKfHandler cpKfHandler;
    private final MpLogHandler mpLogHandler;
    private final MpMsgHandler mpMsgHandler;
    private final ChannelMapper channelMapper;
    private final AccountMapper accountMapper;
    private final WxCpKfMapper wxCpKfMapper;
    private final Map<String, WxCpMessageRouter> cpRouters = Maps.newConcurrentMap();
    private final Map<String, WxCpService> cpServices = Maps.newConcurrentMap();
    private final Map<String, WxMpMessageRouter> mpRouters = Maps.newConcurrentMap();
    private final Map<String, WxMpService> mpServices = Maps.newConcurrentMap();

    @Override
    public WxCpService getCpService(String corpId, Integer agentId) {
        WxCpService cpService = this.cpServices.get(corpId + agentId);
        if (cpService == null) {
            cpService = initCpService(corpId, agentId);
        }
        return Optional.ofNullable(cpService).orElseThrow(() -> new WxRuntimeException("未配置此service"));
    }

    @Override
    public WxCpMessageRouter getCpRouter(String corpId, Integer agentId) {
        return this.cpRouters.get(corpId + agentId);
    }

    @Override
    public void addCpKfServicer(WxCpKfDto switchKfDto) {
        ChannelEntity channelEntity = getChannelEntityByAccountId(switchKfDto.getAccountId());
        WxCpService wxCpService = getCpService(channelEntity.getCorpId(), Integer.valueOf(channelEntity.getAgentId()));
        WxCpKfServiceImpl wxCpKfService = new WxCpKfServiceImpl(wxCpService);
        try {
            WxCpKfServicerOpResp wxCpKfServicerOpResp = wxCpKfService.addServicer(switchKfDto.getOpenKfId(),
                    Lists.newArrayList(switchKfDto.getServiceUserId()));
        } catch (WxErrorException e) {
            log.error("addCpKfServicer", e);
        }
    }

    @Override
    public void listCpKfServicer(WxCpKfDto switchKfDto) {
        ChannelEntity channelEntity = getChannelEntityByAccountId(switchKfDto.getAccountId());
        WxCpService wxCpService = getCpService(channelEntity.getCorpId(), Integer.valueOf(channelEntity.getAgentId()));
        WxCpKfServiceImpl wxCpKfService = new WxCpKfServiceImpl(wxCpService);
        try {
            WxCpKfServicerListResp wxCpKfServicerListRes = wxCpKfService.listServicer(switchKfDto.getOpenKfId());
            wxCpKfServicerListRes.getServicerList().forEach(wxCpKfServicer -> log.info("{}", wxCpKfServicer));
        } catch (WxErrorException e) {
            log.error("switchCpKfServicer", e);
        }
    }

    @Override
    public void switchCpKfServicer(WxCpKfDto switchKfDto) {
        ChannelEntity channelEntity = getChannelEntityByAccountId(switchKfDto.getAccountId());
        WxCpService wxCpService = getCpService(channelEntity.getCorpId(), Integer.valueOf(channelEntity.getAgentId()));
        WxCpKfServiceImpl wxCpKfService = new WxCpKfServiceImpl(wxCpService);
        try {
            wxCpKfService.transServiceState(switchKfDto.getOpenKfId(),
                    switchKfDto.getExternalUserId(),
                    switchKfDto.getServiceState(),
                    switchKfDto.getServiceUserId());
        } catch (WxErrorException e) {
            log.error("switchCpKfServicer", e);
        }
    }

    @Override
    public void switchCpKfServicerByGroupTag(WxCpKfDto switchKfDto) {
        LambdaQueryWrapper<WxKfEntity> queryWrapper = new LambdaQueryWrapper<WxKfEntity>()
                .eq(WxKfEntity::getGroupTag, switchKfDto.getServiceGroup());
        List<WxKfEntity> wxKfEntityList = this.wxCpKfMapper.selectList(queryWrapper);

        switchKfDto.setServiceUserId(wxKfEntityList.get(0).getServiceUserId());
        switchCpKfServicer(switchKfDto);
    }

    private WxCpService initCpService(String corpId, Integer agentId) {
        ChannelEntity channelEntity = getChannelEntity(corpId, agentId);
        if (channelEntity == null) {
            return null;
        }

        WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
        config.setCorpId(channelEntity.getCorpId());
        config.setAgentId(Integer.valueOf(channelEntity.getAgentId()));
        config.setCorpSecret(channelEntity.getSecret());
        config.setToken(channelEntity.getToken());
        config.setAesKey(channelEntity.getAesKey());

        WxCpService service = new WxCpServiceImpl();
        service.setWxCpConfigStorage(config);

        this.cpRouters.put(corpId + agentId, newCpRouter(service));
        this.cpServices.put(corpId + agentId, service);
        return service;
    }

    private WxCpMessageRouter newCpRouter(WxCpService wxCpService) {
        WxCpMessageRouter newRouter = new WxCpMessageRouter(wxCpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.cpLogHandler).next();

//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxCpConsts.EventType.CHANGE_CONTACT).handler(this.contactChangeHandler).end();

        // 信息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).handler(this.cpMsgHandler).end();

        // 客服信息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxCpConsts.EventType.KF_MSG_OR_EVENT).handler(this.cpKfHandler).end();

        return newRouter;
    }

    @Override
    public WxMpService getMpService(String appId) {
        WxMpService mpService = this.mpServices.get(appId);
        if (mpService == null) {
            mpService = initMpService(appId);
        }
        return Optional.ofNullable(mpService).orElseThrow(() -> new WxRuntimeException("未配置此service"));
    }

    @Override
    public WxMpMessageRouter getMpRouter(String appId) {
        // TODO: 2023/12/5
        WxMpConfigStorageHolder.set(appId);
        WxMpMessageRouter mpMessageRouter = this.mpRouters.get(appId);
        if (mpMessageRouter == null) {
            initMpService(appId);
            mpMessageRouter = this.mpRouters.get(appId);
        }
        return mpMessageRouter;
    }

    private WxMpService initMpService(String appId) {
        ChannelEntity channelEntity = getChannelEntity(appId);
        if (channelEntity == null) {
            return null;
        }

        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(channelEntity.getCorpId());
        config.setSecret(channelEntity.getSecret());
        config.setToken(channelEntity.getToken());
        if (StringUtils.hasText(channelEntity.getAesKey())) {
            config.setAesKey(channelEntity.getAesKey());
        }

        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);

        this.mpRouters.put(appId, newMpRouter(wxMpService));
        this.mpServices.put(appId, wxMpService);
        return wxMpService;
    }

    private ChannelEntity getChannelEntity(String corpId) {
        return getChannelEntity(corpId, null);
    }

    private ChannelEntity getChannelEntity(String corpId, Integer agentId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getCorpId, corpId)
                .last("limit 1");
        if (agentId != null) {
            queryWrapper.eq(ChannelEntity::getAgentId, agentId);
        }
        return this.channelMapper.selectOne(queryWrapper);
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

    private WxMpMessageRouter newMpRouter(WxMpService wxMpService) {
        WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.mpLogHandler).next();

        // 信息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).handler(this.mpMsgHandler).end();
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.IMAGE).handler(this.mpMsgHandler).end();

        return newRouter;
    }
}
