package com.github.leapbound.yc.action.service.impl.manage;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.github.leapbound.yc.action.entities.YcFunctionMethod;
import com.github.leapbound.yc.action.mapper.YcFunctionMethodMapper;
import com.github.leapbound.yc.action.model.dto.YcFunctionMethodDto;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionMethodSaveRequest;
import com.github.leapbound.yc.action.service.YcFunctionMethodService;

import java.time.LocalDateTime;

/**
 * @author yamath
 * @since 2023/7/11 10:15
 */
@Service
public class YcFunctionMethodServiceImpl
        extends ServiceImpl<YcFunctionMethodMapper, YcFunctionMethod>
        implements YcFunctionMethodService {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionMethodServiceImpl.class);

    @Override
    public ResponseVo<Void> saveFunctionMethod(FunctionMethodSaveRequest request) {
        // check function method request
        if (checkFunctionMethodRequest(request)) {
            return ResponseVo.fail(null, "缺少 method 信息");
        }
        // select function method
        LambdaQueryWrapper<YcFunctionMethod> wrapper = getFunctionMethodQueryWrapper(request.getFunctionName());
        YcFunctionMethod ycFunctionMethod = baseMapper.selectOne(wrapper);
        // if exist
        if (ycFunctionMethod != null) {
            logger.warn("function method exist, function = {}", request.getId());
            return ResponseVo.fail(null, "数据已经存在");
        }
        // insert
        int rows = this.insertFunctionMethod(request);
        if (rows <= 0) {
            logger.warn("no data insert into [yc_function_method], function = {}", request.getId());
            return ResponseVo.fail(null, "没有数据插入");
        }
        return ResponseVo.success(null);
    }

    @Override
    public ResponseVo<Void> updateFunctionMethod(FunctionMethodSaveRequest request) {
        // check function method request
        if (checkFunctionMethodRequest(request)) {
            return ResponseVo.fail(null, "缺少 method 信息");
        }
        // select function method
        LambdaQueryWrapper<YcFunctionMethod> wrapper = getFunctionMethodQueryWrapper(request.getFunctionName());
        YcFunctionMethod ycFunctionMethod = baseMapper.selectOne(wrapper);
        // if not exist
        if (ycFunctionMethod == null) {
            logger.error("function method not exist, function = {}", request.getId());
            return ResponseVo.fail(null, "没有找到数据");
        }
        // update
        ycFunctionMethod.setFunctionClass(request.getFunctionClass());
        ycFunctionMethod.setFunctionMethod(request.getFunctionMethod());
        ycFunctionMethod.setUpdateUser(request.getUserName());
        ycFunctionMethod.setUpdateTime(LocalDateTime.now());
        int row = baseMapper.updateById(ycFunctionMethod);
        if (row <= 0) {
            logger.warn("no data update from [yc_function_method], function = {}", request.getId());
            return ResponseVo.fail(null, "没有数据更新");
        }
        return ResponseVo.success(null);
    }

    @Override
    public ResponseVo<Void> deleteFunctionMethod(String functionName, String userName) {
        // select function method
        LambdaQueryWrapper<YcFunctionMethod> wrapper = getFunctionMethodQueryWrapper(functionName);
        YcFunctionMethod ycFunctionMethod = baseMapper.selectOne(wrapper);
        // if not exist
        if (ycFunctionMethod == null) {
            logger.error("function method not exist, function = {}", functionName);
            return ResponseVo.fail(null, "没有找到数据");
        }
        // delete
        ycFunctionMethod.setDelFlag(true);
        ycFunctionMethod.setUpdateUser(userName);
        ycFunctionMethod.setUpdateTime(LocalDateTime.now());
        int row = baseMapper.updateById(ycFunctionMethod);
        if (row <= 0) {
            logger.warn("no data delete from [yc_function_method], function = {}", functionName);
            return ResponseVo.fail(null, "没有数据删除");
        }
        return ResponseVo.success(null);
    }

    @Override
    public YcFunctionMethodDto getFunctionMethodDto(String functionName) {
        return baseMapper.selectFunctionMethodDtoByName(functionName);
    }

    private static boolean checkFunctionMethodRequest(FunctionMethodSaveRequest request) {
        if (StrUtil.isEmptyIfStr(request.getFunctionClass())
                || StrUtil.isEmptyIfStr(request.getFunctionMethod())) {
            logger.error("function method is empty");
            return true;
        }
        return false;
    }

    private static LambdaQueryWrapper<YcFunctionMethod> getFunctionMethodQueryWrapper(String functionName) {
        LambdaQueryWrapper<YcFunctionMethod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionMethod::getFunctionName, functionName);
        wrapper.eq(YcFunctionMethod::getDelFlag, 0);
        return wrapper;
    }

    private int insertFunctionMethod(FunctionMethodSaveRequest request) {
        YcFunctionMethod ycFunctionMethod = new YcFunctionMethod();
        ycFunctionMethod.setFunctionName(request.getFunctionName());
        ycFunctionMethod.setFunctionClass(request.getFunctionClass());
        ycFunctionMethod.setFunctionMethod(request.getFunctionMethod());
        ycFunctionMethod.setCreateUser(request.getUserName());
        ycFunctionMethod.setCreateTime(LocalDateTime.now());
        return baseMapper.insert(ycFunctionMethod);
    }
}
