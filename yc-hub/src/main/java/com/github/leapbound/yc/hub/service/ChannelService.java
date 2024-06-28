package com.github.leapbound.yc.hub.service;

import com.github.leapbound.yc.hub.model.ChannelDto;
import com.github.leapbound.yc.hub.model.wx.WxCpKfDto;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;

public interface ChannelService {

    ChannelDto getChannelByAccountId(String accountId);

    WxCpService getCpService(String corpId, Integer agentId);

    WxCpMessageRouter getCpRouter(String corpId, Integer agentId);

    void addCpKfServicer(WxCpKfDto switchKfDto);

    void listCpKfServicer(WxCpKfDto switchKfDto);

    void switchCpKfServicer(WxCpKfDto switchKfDto);

    void switchCpKfServicerByGroupTag(WxCpKfDto switchKfDto);

    WxMpService getMpService(String appId);

    WxMpMessageRouter getMpRouter(String appId);

}
