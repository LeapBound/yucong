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
    @TableField("function_params")
    private String functionParams;
    @TableField("function_description")
    private String functionDescription;
    private String functionJson;
    @TableField("is_extend")
    private boolean extend;
    private Date createTime;
    private Date updateTime;
}
