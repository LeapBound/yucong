package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yc_function_manager")
public class FunctionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String functionName;
    private String functionJson;
    private Date createTime;
    private Date updateTime;
}
