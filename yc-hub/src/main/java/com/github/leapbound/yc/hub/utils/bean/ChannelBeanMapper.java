package com.github.leapbound.yc.hub.utils.bean;

import com.github.leapbound.yc.hub.entities.ChannelEntity;
import com.github.leapbound.yc.hub.model.ChannelDto;

/**
 * @author Fred
 * @date 2024/6/29 0:22
 */
public class ChannelBeanMapper {


    public static ChannelDto mapEntityToModel(ChannelEntity channelEntity) {
        ChannelDto channelDto = new ChannelDto();
        channelDto.setCorpId(channelEntity.getCorpId());
        channelDto.setAgentId(channelEntity.getAgentId());
        return channelDto;
    }
}
