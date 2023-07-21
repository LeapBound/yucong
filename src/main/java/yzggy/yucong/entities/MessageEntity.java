package yzggy.yucong.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yc_message_history")
public class MessageEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("role_str")
    private String role;
    private String content;
    private String name;
}
