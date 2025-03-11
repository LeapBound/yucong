package com.github.leapbound.yc.camunda.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yamath
 * @date 2023/11/17 14:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskReturn {

    private String processInstanceId;

    private String taskId;

    private String taskName;

    private String activityId;

    private List<ProcessStepInputForm> currentInputForm;

    private List<TaskProperties> taskProperties;
}
