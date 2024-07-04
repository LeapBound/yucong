package com.github.leapbound.yc.hub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import com.github.leapbound.yc.hub.entities.RoleEntity;

import java.util.List;

public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("select br.* from yc_bot b " +
            "left join yc_role_relation brr on b.bot_uuid = brr.relate_uuid " +
            "left join yc_role br on brr.role_uuid = br.role_uuid where b.bot_uuid = #{botId}")
    List<RoleEntity> listRoleByBotId(String botId);

    @Insert("insert into yc_role_relation (role_uuid, relate_uuid, relation_type) values (#{roleId}, #{relationId}, #{relationType})")
    void addRoleRelation(String roleId, String relationId, Integer relationType);

    @Select("select count(*) from yc_role_relation where role_uuid = #{roleId} and relate_uuid = #{relationId} and relation_type = #{relationType}")
    boolean checkRoleRelation(String roleId, String relationId, Integer relationType);
}
