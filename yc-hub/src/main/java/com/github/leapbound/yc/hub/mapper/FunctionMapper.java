package com.github.leapbound.yc.hub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import com.github.leapbound.yc.hub.entities.FunctionEntity;

import java.util.List;

public interface FunctionMapper extends BaseMapper<FunctionEntity> {

    @Select("select fm.* " +
            "from yc_bot b " +
            "         left join yc_role_relation rr on b.bot_uuid = rr.relate_uuid " +
            "         left join yc_role_function rf on rr.role_uuid = rf.role_uuid " +
            "         left join yc_function_manage fm on rf.function_uuid = fm.function_uuid " +
            "where b.bot_uuid = #{botId} " +
            "  and rr.relation_type = 0 " +
            "  and fm.id is not null ")
    List<FunctionEntity> listByBotId(String botId);

    @Select("select fm.* " +
            "from yc_account a " +
            "         left join yc_role_relation rr on a.id = rr.relate_id " +
            "         left join yc_role_function rf on rr.role_id = rf.role_id " +
            "         left join yc_function_manage fm on rf.function_id = fm.id " +
            "where a.account_name = #{accountId} " +
            "  and rr.relation_type = 1 " +
            "  and fm.id is not null ")
    List<FunctionEntity> listByAccountId(String accountId);
}
