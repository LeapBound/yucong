package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yc_account")
public class AccountEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("account_uuid")
    private String accountId;
    @TableField("account_name")
    private String externalId;
    @TableField("user_uuid")
    private String userId;
    @TableField("bot_uuid")
    private String botId;
    private Date createTime;
    private Date updateTime;
}
