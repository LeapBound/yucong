package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yc_function_manage")
public class FunctionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("function_uuid")
    private String functionUuid;
    private String functionName;
    private String functionJson;
    private Date createTime;
    private Date updateTime;
}
