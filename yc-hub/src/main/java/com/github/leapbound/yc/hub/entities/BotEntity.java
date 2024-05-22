package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yc_bot")
public class BotEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("bot_uuid")
    private String botId;
    private String botName;
    private String initRoleContent;
    private Date createTime;
}
