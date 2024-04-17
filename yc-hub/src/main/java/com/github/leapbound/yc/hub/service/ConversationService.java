package com.github.leapbound.yc.hub.service;

import com.github.leapbound.sdk.llm.chat.dialog.MessageMqTrans;
import com.github.leapbound.sdk.llm.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.model.SingleChatDto;

import java.util.List;

public interface ConversationService {

    List<MyMessage> getByConversationId(String conversationId);

    List<MyMessage> getByBotIdAndAccountId(String botId, String accountId);

    MyMessage chat(SingleChatDto singleChatModel);

    void notifyUser(SingleChatDto singleChatModel);

    void summaryDialog(String conversationId);

    void clearMessageHistory(String botId, String accountId);

    void persistMessage(MessageMqTrans messageList);
}
