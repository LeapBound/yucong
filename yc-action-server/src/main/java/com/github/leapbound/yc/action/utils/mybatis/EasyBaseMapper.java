package com.github.leapbound.yc.action.utils.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @author yamath
 * @date 2023/7/10 13:12
 */
public interface EasyBaseMapper<T> extends BaseMapper<T> {

    int insertBatchSomeColumn(Collection<T> entityList);
}
