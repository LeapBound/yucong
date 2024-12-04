package com.github.leapbound.yc.hub.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimDto {

    private Long appId;
    private String userId;
    private String userSig;
}
