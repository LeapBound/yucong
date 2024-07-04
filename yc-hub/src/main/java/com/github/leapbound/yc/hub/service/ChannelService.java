package com.github.leapbound.yc.hub.service;

import com.github.leapbound.yc.hub.model.ChannelDto;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;

public interface ChannelService {

    ChannelDto getChannelByAccountId(String accountId);

    ChannelDto getChannelByOpenKfId(String openKfId);

    ChannelDto getChannelByCorpIdAndAgentId(String corpId, String agentId);
}
