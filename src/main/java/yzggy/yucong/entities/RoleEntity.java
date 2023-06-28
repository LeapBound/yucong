package yzggy.yucong.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("yc_role")
public class RoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;

    @TableField(exist = false)
    private List<String> authorityList;
}
