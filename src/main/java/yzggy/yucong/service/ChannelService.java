package yzggy.yucong.service;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;

public interface ChannelService {

    WxCpService getCpService(String corpId, Integer agentId);

    WxCpMessageRouter getRouter(String corpId, Integer agentId);

}
