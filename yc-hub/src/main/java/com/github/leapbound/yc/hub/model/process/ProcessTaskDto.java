package com.github.leapbound.yc.hub.model.process;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProcessTaskDto {

    private String processInstanceId;
    private String taskId;
    private String taskName;
    private List<ProcessFormFieldDto> currentInputForm;
    private List<Map<String, Object>> taskProperties;

}
