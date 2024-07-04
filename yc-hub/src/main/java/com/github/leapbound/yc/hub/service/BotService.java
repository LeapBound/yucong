package com.github.leapbound.yc.hub.service;

import com.github.leapbound.yc.hub.model.BotDto;

import java.util.List;

public interface BotService {

    List<BotDto> listAll();

    String getBotId(String corpId, String agentId);

    String getBotId(String appId);

    Long getBotNIdByBotId(String botId);

    BotDto getBotByBotId(String botId);

    void create(BotDto botModel);


}
