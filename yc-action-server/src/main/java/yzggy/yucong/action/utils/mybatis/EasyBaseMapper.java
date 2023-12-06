package yzggy.yucong.action.utils.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @author yamath
 * @since 2023/7/10 13:12
 */
public interface EasyBaseMapper<T> extends BaseMapper<T> {

    int insertBatchSomeColumn(Collection<T> entityList);
}
