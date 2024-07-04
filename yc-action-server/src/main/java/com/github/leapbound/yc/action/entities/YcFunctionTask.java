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
 * @date 2024/3/29 14:59
 */
@Data
@TableName("yc_function_task")
public class YcFunctionTask implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * process id
     */
    private String processId;

    /**
     * function name
     */
    private String functionName;

    /**
     *  task name
     */
    private String taskName;

    /**
     * task type
     */
    private String taskType;

    /**
     * task json
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
