package com.github.leapbound.yc.hub.service;

import com.github.leapbound.yc.hub.chat.dialog.MessageMqTrans;
import com.github.leapbound.yc.hub.chat.dialog.MyMessage;
import com.github.leapbound.yc.hub.model.SingleChatDto;

import java.util.List;

public interface ConversationService {

    List<MyMessage> getByConversationId(String conversationId);

    List<MyMessage> getByBotIdAndAccountId(String botId, String externalId);

    MyMessage chat(SingleChatDto singleChatModel);

    MyMessage chat(SingleChatDto singleChatModel, Boolean isTest);

    void notifyUser(SingleChatDto singleChatModel);

    Boolean checkNotify(String accountId);

    void summaryDialog(String conversationId);

    void clearMessageHistory(String botId, String accountId);

    void persistMessage(MessageMqTrans messageList);
}
