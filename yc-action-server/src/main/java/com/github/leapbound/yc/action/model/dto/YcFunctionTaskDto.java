package com.github.leapbound.yc.action.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author tangxu
 * @since 2024/3/29 17:47
 */
@Data
public class YcFunctionTaskDto implements Serializable {

    private String functionName;

    private String taskName;

    private String speechcraft;

}
