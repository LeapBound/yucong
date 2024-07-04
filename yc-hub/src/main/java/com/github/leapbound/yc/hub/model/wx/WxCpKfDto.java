package com.github.leapbound.yc.hub.model.wx;

import lombok.Data;

/**
 * @author Fred
 * @date 2024/6/27 16:25
 */
@Data
public class WxCpKfDto {

    private String accountId;
    private String openKfId;
    private String externalUserId;
    private Integer serviceState;
    private String serviceUserId;
    private String serviceGroup;
}
