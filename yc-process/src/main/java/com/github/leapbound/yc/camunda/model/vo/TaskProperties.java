package com.github.leapbound.yc.camunda.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yamath
 * @since 2023/11/23 11:43
 */
@Data
@NoArgsConstructor
public class TaskProperties {

    private String name;

    private Object type;
}
