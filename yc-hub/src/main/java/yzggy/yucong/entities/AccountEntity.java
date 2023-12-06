package yzggy.yucong.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yc_account")
public class AccountEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String accountName;
    private Long userId;
    private Long botId;
    private Date createTime;
    private Date updateTime;
}
