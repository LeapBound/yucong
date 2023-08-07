package yzggy.yucong.chat.dialog;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;

@Data
public class MessageMqTrans {

    private String conversationId;
    private String botId;
    private String accountId;
    private Message message;
}
