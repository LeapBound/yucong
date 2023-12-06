package yzggy.yucong.model;

import lombok.Data;

import java.util.Date;

@Data
public class BotModel {

    private String botId;
    private String botName;
    private String initContent;
    private Date createTime;
}
