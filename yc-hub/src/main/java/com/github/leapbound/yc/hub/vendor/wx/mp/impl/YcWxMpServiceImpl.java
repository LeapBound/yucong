package com.github.leapbound.yc.hub.vendor.wx.mp.impl;

import com.github.leapbound.yc.hub.model.ChannelDto;
import com.github.leapbound.yc.hub.service.ChannelService;
import com.github.leapbound.yc.hub.vendor.wx.mp.MpLogHandler;
import com.github.leapbound.yc.hub.vendor.wx.mp.MpMsgHandler;
import com.github.leapbound.yc.hub.vendor.wx.mp.YcWxMpService;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author Fred
 * @date 2024/7/1 13:16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YcWxMpServiceImpl implements YcWxMpService {

    private final MpLogHandler mpLogHandler;
    private final MpMsgHandler mpMsgHandler;

    private final ChannelService channelService;

    private final Map<String, WxMpMessageRouter> mpRouters = Maps.newConcurrentMap();
    private final Map<String, WxMpService> mpServices = Maps.newConcurrentMap();

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
        // TODO: 2023/12/5 替换掉appId
        WxMpConfigStorageHolder.set(appId);
        WxMpMessageRouter mpMessageRouter = this.mpRouters.get(appId);
        if (mpMessageRouter == null) {
            initMpService(appId);
            mpMessageRouter = this.mpRouters.get(appId);
        }
        return mpMessageRouter;
    }

    private WxMpService initMpService(String appId) {
        ChannelDto channelDto = this.channelService.getChannelByAccountId(appId);
        if (channelDto == null) {
            return null;
        }

        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(channelDto.getCorpId());
        config.setSecret(channelDto.getSecret());
        config.setToken(channelDto.getToken());
        if (StringUtils.hasText(channelDto.getAesKey())) {
            config.setAesKey(channelDto.getAesKey());
        }

        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);

        this.mpRouters.put(appId, newMpRouter(wxMpService));
        this.mpServices.put(appId, wxMpService);
        return wxMpService;
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
