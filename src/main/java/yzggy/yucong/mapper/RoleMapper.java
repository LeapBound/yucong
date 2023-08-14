package yzggy.yucong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import yzggy.yucong.entities.RoleEntity;

import java.util.List;

public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("select br.* from yc_bot b " +
            "left join yc_role_relation brr on b.id = brr.relate_id " +
            "left join yc_role br on brr.role_id = br.id where b.bot_id = #{botId}")
    List<RoleEntity> listRoleByBotId(String botId);

    @Insert("insert into yc_role_relation (role_id, relate_id, relation_type) values (#{roleId}, #{relationId}, #{relationType})")
    void addRoleRelation(Long roleId, Long relationId, Integer relationType);

    @Select("select count(*) from yc_role_relation where role_id = #{roleId} and relate_id = #{relationId} and relation_type = #{relationType}")
    boolean checkRoleRelation(Long roleId, Long relationId, Integer relationType);
}
