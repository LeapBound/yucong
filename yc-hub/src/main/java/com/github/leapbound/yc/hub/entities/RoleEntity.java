package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yc_role")
public class RoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleName;
}
