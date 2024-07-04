package com.github.leapbound.yc.hub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fred
 * @date 2024/7/3 12:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionExecResultDto {

    private Boolean executeResult;
    private String msg;
}
