package yzggy.yucong.action.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import yzggy.yucong.action.entities.YcFunctionMethod;
import yzggy.yucong.action.model.dto.YcFunctionMethodDto;
import yzggy.yucong.action.utils.mybatis.EasyBaseMapper;

/**
 * <p>
 * function methods Mapper 接口
 * </p>
 *
 * @author yamath
 * @since 2023-07-11
 */
@Mapper
public interface YcFunctionMethodMapper extends EasyBaseMapper<YcFunctionMethod> {
    String functionMethodDtoSql = "SELECT function_name as `functionName`," +
            " function_class as `functionClass`, function_method as `functionMethod`" +
            " FROM yc_function_method" +
            " WHERE del_flag = 0 AND function_name = '${functionName}'";

    @Select(functionMethodDtoSql)
    YcFunctionMethodDto selectFunctionMethodDtoByName(@Param("functionName") String functionName);
}
