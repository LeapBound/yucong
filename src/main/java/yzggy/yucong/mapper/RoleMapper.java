package yzggy.yucong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import yzggy.yucong.entities.RoleEntity;

import java.util.List;

public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("select br.* from yc_bot b " +
            "left join yc_bot_role_relation brr on b.id = brr.bot_id " +
            "left join yc_bot_role br on brr.role_id = br.id where b.bot_id = #{botId}")
    List<RoleEntity> selectRoleByBotId(String botId);
}
