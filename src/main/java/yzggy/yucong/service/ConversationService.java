package yzggy.yucong.service;

import yzggy.yucong.chat.dialog.MessageMqTrans;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.model.SingleChatModel;

import java.util.List;

public interface ConversationService {

    List<MyMessage> getByConversationId(String conversationId);

    List<MyMessage> getByBotIdAndAccountId(String botId, String accountId);

    String chat(SingleChatModel singleChatModel);

    void summaryDialog(String conversationId);

    void clearMessageHistory(String botId, String accountId);

    void persistMessage(MessageMqTrans messageList);
}
