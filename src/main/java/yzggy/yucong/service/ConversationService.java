package yzggy.yucong.service;

import yzggy.yucong.chat.dialog.MessageMqTrans;
import yzggy.yucong.chat.dialog.MyMessage;

import java.util.List;

public interface ConversationService {

    List<MyMessage> getByConversationId(String conversationId);

    List<MyMessage> getByBotIdAndAccountId(String botId, String accountId);

    Boolean start(String botId, String accountId);

    void addMessage(String botId, String accountId, MyMessage messages);

    void clearMessageHistory(String botId, String accountId);

    void persistMessage(MessageMqTrans messageList);

    String findContentOfDialogByConversationId(String conversationId);
}
