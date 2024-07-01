package com.github.leapbound.yc.hub.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author Fred
 * @date 2024/6/29 0:20
 */
@Data
public class ChannelDto {

    private String botId;
    private String channelId;

    private String corpId;
    private String agentId;
    private String secret;
    private String token;
    private String aesKey;

}
