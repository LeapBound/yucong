package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.Message;
import yzggy.yucong.chat.dialog.MessageMqTrans;

import java.util.List;

public interface ConversationService {

    List<Message> getByConversationId(String conversationId);

    List<Message> getByBotIdAndAccountId(String botId, String accountId);

    Boolean start(String botId, String accountId);

    void addMessage(String botId, String accountId, Message messages);


    void persistMessage(MessageMqTrans messageList);

}
