package com.github.leapbound.yc.hub.vendor.wx.mp;

import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * @author Fred
 * @date 2024/7/1 13:16
 */
public interface YcWxMpService {

    WxMpService getMpService(String appId);

    WxMpMessageRouter getMpRouter(String appId);

}
