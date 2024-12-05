package com.github.leapbound.yc.hub.utils.bean;

import com.github.leapbound.yc.hub.entities.ChannelEntity;
import com.github.leapbound.yc.hub.model.ChannelDto;

/**
 * @author Fred
 * @date 2024/6/29 0:22
 */
public class ChannelBeanMapper {

    public static ChannelDto mapEntityToModel(ChannelEntity entity) {
        ChannelDto dto = new ChannelDto();

        dto.setBotId(entity.getBotId());
        dto.setChannelId(entity.getChannelId());

        dto.setCorpId(entity.getCorpId());
        dto.setAgentId(entity.getAgentId());
        dto.setSecret(entity.getSecret());
        dto.setToken(entity.getToken());
        dto.setAesKey(entity.getAesKey());

        return dto;
    }
}
