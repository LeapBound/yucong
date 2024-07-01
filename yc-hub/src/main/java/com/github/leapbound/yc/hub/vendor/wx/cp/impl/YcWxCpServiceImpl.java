package com.github.leapbound.yc.hub.vendor.wx.cp.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.leapbound.yc.hub.entities.WxKfEntity;
import com.github.leapbound.yc.hub.mapper.WxCpKfMapper;
import com.github.leapbound.yc.hub.model.ChannelDto;
import com.github.leapbound.yc.hub.model.wx.WxCpKfDto;
import com.github.leapbound.yc.hub.service.ChannelService;
import com.github.leapbound.yc.hub.vendor.wx.cp.YcWxCpService;
import com.github.leapbound.yc.hub.vendor.wx.cp.handler.CpKfHandler;
import com.github.leapbound.yc.hub.vendor.wx.cp.handler.CpKfTestHandler;
import com.github.leapbound.yc.hub.vendor.wx.cp.handler.CpLogHandler;
import com.github.leapbound.yc.hub.vendor.wx.cp.handler.CpMsgHandler;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Fred
 * @date 2024/7/1 11:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YcWxCpServiceImpl implements YcWxCpService {

    private final Map<String, WxCpMessageRouter> cpRouters = Maps.newConcurrentMap();
    private final Map<String, WxCpService> cpServices = Maps.newConcurrentMap();

    private final WxCpKfMapper wxCpKfMapper;

    private final CpLogHandler cpLogHandler;
    private final CpMsgHandler cpMsgHandler;
    private final CpKfHandler cpKfHandler;
    private final CpKfTestHandler cpKfTestHandler;

    private final ChannelService channelService;

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
        ChannelDto channelDto = this.channelService.getChannelByAccountId(switchKfDto.getAccountId());
        WxCpService wxCpService = getCpService(channelDto.getCorpId(), Integer.valueOf(channelDto.getAgentId()));
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
        ChannelDto channelDto = this.channelService.getChannelByAccountId(switchKfDto.getAccountId());
        WxCpService wxCpService = getCpService(channelDto.getCorpId(), Integer.valueOf(channelDto.getAgentId()));
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
        ChannelDto channelDto = this.channelService.getChannelByAccountId(switchKfDto.getAccountId());
        WxCpService wxCpService = getCpService(channelDto.getCorpId(), Integer.valueOf(channelDto.getAgentId()));
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
        ChannelDto channelDto = this.channelService.getChannelByCorpIdAndAgentId(corpId, String.valueOf(agentId));
        if (channelDto == null) {
            return null;
        }

        WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
        config.setCorpId(channelDto.getCorpId());
        config.setAgentId(Integer.valueOf(channelDto.getAgentId()));
        config.setCorpSecret(channelDto.getSecret());
        config.setToken(channelDto.getToken());
        config.setAesKey(channelDto.getAesKey());

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

        // 信息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).handler(this.cpMsgHandler).end();

        // 客服信息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxCpConsts.EventType.KF_MSG_OR_EVENT).handler(this.cpKfHandler).end();
        // 测试客服信息
        newRouter.rule().async(false).msgType("yc_test_event")
                .event(WxCpConsts.EventType.KF_MSG_OR_EVENT).handler(this.cpKfTestHandler).end();

        return newRouter;
    }
}
