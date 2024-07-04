package com.github.leapbound.yc.hub.model.process;

import lombok.Data;

import java.util.Map;

/**
 * @author Fred
 * @date 2024/4/8 10:45
 */
@Data
public class ProcessRequestDto {

    private String processInstanceId;
    private String businessKey;
    private Map<String, Object> inputVariables;
}
