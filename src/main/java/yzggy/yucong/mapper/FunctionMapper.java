package yzggy.yucong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import yzggy.yucong.entities.FunctionEntity;

import java.util.List;

public interface FunctionMapper extends BaseMapper<FunctionEntity> {

    @Select("select fm.* " +
            "from yc_account a " +
            "         left join yc_role_relation rr on a.id = rr.relate_id " +
            "         left join yc_role_function rf on rr.role_id = rf.role_id " +
            "         left join yc_function_manage fm on rf.function_id = fm.id " +
            "where a.account_name = #{accountId} " +
            "  and rr.relation_type = 1 ")
    List<FunctionEntity> listByAccountId(String accountId);

    @Select("select fm.*" +
            "from yc_task_function tf " +
            "         left join yc_function_manage fm on tf.function_id = fm.id " +
            "where tf.task_name = #{taskName}")
    List<FunctionEntity> listByTaskName(String taskName);
}
