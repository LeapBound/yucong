package yzggy.yucong.chat.dialog;

import lombok.Data;

import java.util.Date;

@Data
public class MessageMqTrans {

    private String conversationId;
    private String botId;
    private String accountId;
    private MyMessage message;
    private Date createTime;
}
