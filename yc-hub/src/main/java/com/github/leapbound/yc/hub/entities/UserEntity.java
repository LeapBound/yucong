package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yc_user")
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_uuid")
    private String userId;
    private String username;
    private Date createTime;
}
