package com.github.leapbound.yc.hub.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Fred
 * @date 2024/6/28 15:41
 */
@Data
@TableName("yc_wx_cp_kf")
public class WxKfEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String groupTag;
    private String serviceUserId;
    private Date createTime;

}
