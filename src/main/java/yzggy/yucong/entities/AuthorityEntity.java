package yzggy.yucong.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yc_authority")
public class AuthorityEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String authorityName;
}
