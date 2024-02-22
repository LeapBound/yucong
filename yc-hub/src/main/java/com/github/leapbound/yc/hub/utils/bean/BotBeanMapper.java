package com.github.leapbound.yc.hub.utils.bean;

import com.github.leapbound.yc.hub.entities.BotEntity;
import com.github.leapbound.yc.hub.model.BotModel;

public class BotBeanMapper {

    public static BotEntity mapModelToEntity(BotModel botModel) {
        BotEntity botEntity = new BotEntity();
        botEntity.setBotId(botModel.getBotId());
        botEntity.setBotName(botModel.getBotName());
        botEntity.setInitRoleContent(botModel.getInitContent());
        return botEntity;
    }
}
