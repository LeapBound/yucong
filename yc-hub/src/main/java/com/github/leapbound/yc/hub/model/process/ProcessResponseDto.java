package com.github.leapbound.yc.hub.model.process;

import lombok.Data;

/**
 * @author Fred
 * @date 2024/4/8 13:28
 */
@Data
public class ProcessResponseDto<T> {

    private Boolean success;
    private Integer code;
    private T data;
    /*
     * error message
     */
    private String msg;
}
