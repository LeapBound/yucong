package yzggy.yucong.chat.dialog;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Conversation {

    private List<Message> messageList;

    public void addMessage(Message message) {
        if (this.messageList == null) {
            this.messageList = new ArrayList<>();
        }

        this.messageList.add(message);
    }
}
