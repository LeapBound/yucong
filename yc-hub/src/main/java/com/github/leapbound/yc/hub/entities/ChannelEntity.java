package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

@Data
@TableName("yc_channel_config")
public class ChannelEntity {

    @Getter
    @TableId(type = IdType.AUTO)
    private Long id;
    private String botId;
    private String agentId;
    private String corpId;
    @TableField(value = "secret_content")
    private String secret;
    @TableField(value = "token_content")
    private String token;
    private String aesKey;
}
