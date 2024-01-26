package yzggy.yucong.action.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import yzggy.yucong.action.entities.YcFunctionManage;
import yzggy.yucong.action.model.dto.YcFunctionManageDto;
import yzggy.yucong.action.utils.mybatis.EasyBaseMapper;

import java.util.List;

/**
 * <p>
 * yc function management Mapper 接口
 * </p>
 *
 * @author yamath
 * @since 2023-07-11
 */
@Mapper
public interface YcFunctionManageMapper extends EasyBaseMapper<YcFunctionManage> {

    String functionManageByRoleSql = """
            <script>
            SELECT DISTINCT
             t2.id as `id`,
             t2.function_name as `functionName`,
             t2.function_json as `functionJson`,
             t2.function_use as `functionUse`,
             t2.function_uid as `functionUid`
             FROM yc_function_role t1
             LEFT JOIN (SELECT * FROM yc_function_manage WHERE del_flag = 0 AND function_use = 1) t2
             ON t1.function_name = t2.function_name
             WHERE t1.del_flag = 0 AND t2.id IS NOT NULL AND t1.role_id in""" +
            "     <foreach item='item' index='index' collection='roleIdList' open='(' separator=',' close=')'>" +
            "         #{item}" +
            "     </foreach>" +
            "</script>";

    @Select(functionManageByRoleSql)
    List<YcFunctionManageDto> selectFunctionManageByRole(@Param("roleIdList") List<String> roleIdList);
}
