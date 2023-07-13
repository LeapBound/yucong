package yzggy.yucong.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yc_bot")
public class BotEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String botId;
    private String initRoleContent;
}
