package com.github.leapbound.yc.hub.service;

import com.github.leapbound.yc.hub.model.BotModel;

import java.util.List;

public interface BotService {

    List<BotModel> listAll();

    String getBotId(String corpId, String agentId);

    String getBotId(String appId);

    Long getBotNIdByBotId(String botId);

    void create(BotModel botModel);


}
