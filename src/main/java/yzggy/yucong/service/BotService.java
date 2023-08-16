package yzggy.yucong.service;

import yzggy.yucong.model.BotModel;

import java.util.List;

public interface BotService {

    List<BotModel> listAll();

    String getBotId(String corpId, String agentId);

    Long getBotNIdByBotId(String botId);

    void create(BotModel botModel);
}
