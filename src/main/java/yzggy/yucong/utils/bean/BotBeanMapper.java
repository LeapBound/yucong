package yzggy.yucong.utils.bean;

import yzggy.yucong.entities.BotEntity;
import yzggy.yucong.model.BotModel;

public class BotBeanMapper {

    public static BotEntity mapModelToEntity(BotModel botModel) {
        BotEntity botEntity = new BotEntity();
        botEntity.setBotId(botModel.getBotId());
        botEntity.setBotName(botModel.getBotName());
        botEntity.setInitRoleContent(botModel.getInitContent());
        return botEntity;
    }
}
