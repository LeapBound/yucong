package yzggy.yucong.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yc_bot_role")
public class RoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleName;
}
