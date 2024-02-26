package com.github.leapbound.yc.action.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.github.leapbound.yc.action.entities.YcFunctionGroovy;
import com.github.leapbound.yc.action.model.dto.YcFunctionGroovyDto;
import com.github.leapbound.yc.action.utils.mybatis.EasyBaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author yamath
 * @since 2023-10-12
 */
@Mapper
public interface YcFunctionGroovyMapper extends EasyBaseMapper<YcFunctionGroovy> {

    String functionGroovyDtoSql = "SELECT function_name as `functionName`," +
            " groovy_name as `groovyName`, groovy_url as `groovyUrl`" +
            " FROM yc_function_groovy" +
            " WHERE del_flag = 0 AND function_name = '${functionName}'";

    String groovyNameSql = """
            <script>
            SELECT DISTINCT 
            groovy_name as `groovyName`,
            groovy_url as `groovyUrl`
            FROM yc_function_groovy
            WHERE del_flag = 0 """ +
            " <if test= \"groovyName != null and groovyName != ''\"> " +
            "  AND groovy_name = #{groovyName}" +
            " </if> " +
            "</script>";

    @Select(functionGroovyDtoSql)
    YcFunctionGroovyDto selectFunctionGroovyDtoByName(@Param("functionName") String functionName);

    @Select(groovyNameSql)
    YcFunctionGroovyDto selectFunctionGroovyDtoByGroovy(@Param("groovyName") String groovyName);

    @Select(groovyNameSql)
    List<YcFunctionGroovyDto> selectFunctionGroovyDtoListByGroovy();
}
