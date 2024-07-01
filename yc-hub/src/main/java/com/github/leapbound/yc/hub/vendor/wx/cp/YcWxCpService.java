package com.github.leapbound.yc.hub.vendor.wx.cp;

import com.github.leapbound.yc.hub.model.wx.WxCpKfDto;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;

/**
 * @author Fred
 * @date 2024/7/1 11:56
 */
public interface YcWxCpService {

    WxCpService getCpService(String corpId, Integer agentId);

    WxCpMessageRouter getCpRouter(String corpId, Integer agentId);

    void addCpKfServicer(WxCpKfDto switchKfDto);

    void listCpKfServicer(WxCpKfDto switchKfDto);

    void switchCpKfServicer(WxCpKfDto switchKfDto);

    void switchCpKfServicerByGroupTag(WxCpKfDto switchKfDto);

}
