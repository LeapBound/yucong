package com.github.leapbound.yc.hub.service;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;

public interface ChannelService {

    WxCpService getCpService(String corpId, Integer agentId);

    WxCpMessageRouter getCpRouter(String corpId, Integer agentId);

    WxMpService getMpService(String appId);

    WxMpMessageRouter getMpRouter(String appId);

}
