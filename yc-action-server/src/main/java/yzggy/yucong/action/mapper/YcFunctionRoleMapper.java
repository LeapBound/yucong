package yzggy.yucong.action.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import yzggy.yucong.action.entities.YcFunctionRole;
import yzggy.yucong.action.utils.mybatis.EasyBaseMapper;

import java.util.List;

/**
 * <p>
 * function role Mapper 接口
 * </p>
 *
 * @author yamath
 * @since 2023-07-11
 */
@Mapper
public interface YcFunctionRoleMapper extends EasyBaseMapper<YcFunctionRole> {
    String getRoleIdListSql = "SELECT role_id FROM yc_function_role where del_flag = 0 group by role_id";

    @Select(getRoleIdListSql)
    List<String> selectFunctionRoleIdList();
}
