package yzggy.yucong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yzggy.yucong.entities.ChannelEntity;
import yzggy.yucong.handler.wx.cp.CpLogHandler;
import yzggy.yucong.handler.wx.cp.CpMsgHandler;
import yzggy.yucong.handler.wx.mp.MpLogHandler;
import yzggy.yucong.handler.wx.mp.MpMsgHandler;
import yzggy.yucong.mapper.ChannelMapper;
import yzggy.yucong.service.ChannelService;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final CpLogHandler cpLogHandler;
    private final CpMsgHandler cpMsgHandler;
    private final MpLogHandler mpLogHandler;
    private final MpMsgHandler mpMsgHandler;
    private final ChannelMapper channelMapper;
    private final Map<String, WxCpMessageRouter> cpRouters = Maps.newHashMap();
    private final Map<String, WxCpService> cpServices = Maps.newHashMap();
    private final Map<String, WxMpMessageRouter> mpRouters = Maps.newHashMap();
    private final Map<String, WxMpService> mpServices = Maps.newHashMap();

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

    private WxCpService initCpService(String corpId, Integer agentId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getCorpId, corpId)
                .eq(ChannelEntity::getAgentId, agentId)
                .last("limit 1");
        ChannelEntity channelEntity = this.channelMapper.selectOne(queryWrapper);
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

    private WxMpService initMpService(String appId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getCorpId, appId)
                .last("limit 1");
        ChannelEntity channelEntity = this.channelMapper.selectOne(queryWrapper);
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

    private WxCpMessageRouter newCpRouter(WxCpService wxCpService) {
        WxCpMessageRouter newRouter = new WxCpMessageRouter(wxCpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.cpLogHandler).next();

//        // 自定义菜单事件
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.MenuButtonType.CLICK).handler(this.menuHandler).end();
//
//        // 点击菜单链接事件（这里使用了一个空的处理器，可以根据自己需要进行扩展）
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.MenuButtonType.VIEW).handler(this.nullHandler).end();
//
//        // 关注事件
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.EventType.SUBSCRIBE).handler(this.subscribeHandler)
//                .end();
//
//        // 取消关注事件
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.EventType.UNSUBSCRIBE)
//                .handler(this.unsubscribeHandler).end();
//
//        // 上报地理位置事件
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.EventType.LOCATION).handler(this.locationHandler)
//                .end();
//
//        // 接收地理位置消息
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION)
//                .handler(this.locationHandler).end();
//
//        // 扫码事件（这里使用了一个空的处理器，可以根据自己需要进行扩展）
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.EventType.SCAN).handler(this.nullHandler).end();
//
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxCpConsts.EventType.CHANGE_CONTACT).handler(this.contactChangeHandler).end();
//
//        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxCpConsts.EventType.ENTER_AGENT).handler(new EnterAgentHandler()).end();

        // 信息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).handler(this.cpMsgHandler).end();

//        newRouter.rule().async(false).handler(this.msgOrderHandler).end();

        return newRouter;
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
