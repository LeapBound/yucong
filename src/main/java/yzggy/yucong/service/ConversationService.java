package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.Message;
import yzggy.yucong.chat.dialog.Conversation;

import java.util.List;

public interface ConversationService {

    Conversation getByAccountId(String accountId);

    Conversation start(String botId, String accountId);

    void addMessage(String accountId, Message... messages);

    void addMessages(String accountId, List<Message> messageList);

}
