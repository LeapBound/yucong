package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yc_message_summary")
public class MessageSummaryEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String conversationId;
    private String content;
    private Date createTime;
}
