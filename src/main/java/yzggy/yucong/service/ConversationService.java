package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.Message;
import yzggy.yucong.chat.dialog.Conversation;

import java.util.List;
import java.util.Map;

public interface ConversationService {

    Conversation getByAccountId(String accountId);

    Conversation start(String botId, String accountId);

    void addMessage(String accountId, Message... messages);

    void addMessages(String accountId, List<Message> messageList);

    void persistMessage(List<Message> messageList);

    void persistMessageMap(List<Map<String, Object>> messageList);

}
