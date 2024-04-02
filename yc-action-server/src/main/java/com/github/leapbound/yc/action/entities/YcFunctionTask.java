package com.github.leapbound.yc.action.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * function link task service info
 *
 * @author tangxu
 * @since 2024/3/29 14:59
 */
@Data
@TableName("yc_function_task")
public class YcFunctionTask implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 方法名
     */
    private String functionName;

    /**
     *  task name  -> 流程管理
     */
    private String taskName;

    /**
     * 话术 json 字符串
     */
    private String script;

    /**
     * 0=action >0=deleted
     */
    private Boolean delFlag;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;

}
