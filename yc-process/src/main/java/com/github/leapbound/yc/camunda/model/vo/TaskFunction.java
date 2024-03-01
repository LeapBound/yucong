package com.github.leapbound.yc.camunda.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yamath
 * @since 2023/11/24 11:32
 */
@Data
@NoArgsConstructor
public class TaskFunction implements Serializable {

    private String activityId;

    private String functionName;
}
