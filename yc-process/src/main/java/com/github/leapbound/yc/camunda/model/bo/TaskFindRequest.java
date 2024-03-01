package com.github.leapbound.yc.camunda.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yamath
 * @since 2023/11/17 14:54
 */
@Data
@NoArgsConstructor
public class TaskFindRequest {

    private String processInstanceId;

    private String key;

    private String businessKey;
}
