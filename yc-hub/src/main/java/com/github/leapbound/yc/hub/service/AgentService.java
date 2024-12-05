package com.github.leapbound.yc.hub.service;

import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;

import java.util.List;
import java.util.Map;

/**
 * @author Fred Gu
 * @date 2024-12-04 10:10
 */
public interface AgentService {

    List<MyMessage> completions(String botId, String accountId, Map<String, Object> params, List<MyMessage> messageList, Boolean isTest);
}
