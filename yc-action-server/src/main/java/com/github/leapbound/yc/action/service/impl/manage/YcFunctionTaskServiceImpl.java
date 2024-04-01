package com.github.leapbound.yc.action.service.impl.manage;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.leapbound.yc.action.entities.YcFunctionTask;
import com.github.leapbound.yc.action.mapper.YcFunctionTaskMapper;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionTaskRequest;
import com.github.leapbound.yc.action.service.YcFunctionTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 *
 *
 * @author tangxu
 * @since 2024/3/29 15:17
 */
@Service
public class YcFunctionTaskServiceImpl
        extends ServiceImpl<YcFunctionTaskMapper, YcFunctionTask>
        implements YcFunctionTaskService {
    private static final Logger logger = LoggerFactory.getLogger(YcFunctionTaskServiceImpl.class);

    @Override
    public YcFunctionTask queryFunctionTask(String functionName) {
        LambdaQueryWrapper<YcFunctionTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionTask::getFunctionName, functionName);
        wrapper.eq(YcFunctionTask::getDelFlag, 0);
        YcFunctionTask functionTask = baseMapper.selectOne(wrapper);
        if (null == functionTask) {
            logger.warn("function task not exist, functionName = {}", functionName);
            return null;
        }
        return functionTask;
    }

    @Override
    public ResponseVo<Void> saveFunctionTask(FunctionTaskRequest request) {
        // check function task request
        if (checkFunctionGroovyRequest(request)) {
            return ResponseVo.fail(null, "缺少 task 信息");
        }
        LambdaQueryWrapper<YcFunctionTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionTask::getFunctionName, request.getFunctionName());
        wrapper.eq(YcFunctionTask::getTaskName, request.getTaskName());
        wrapper.eq(YcFunctionTask::getDelFlag, 0);
        YcFunctionTask functionTask = baseMapper.selectOne(wrapper);
        if (null != functionTask) {
            logger.warn("function task exist, function = {}", request.getId());
            return ResponseVo.fail(null, "数据已经存在");
        }
        // insert
        int rows = this.insertFunctionTask(request);
        if (rows <= 0) {
            logger.warn("no data insert into [yc_function_task], function = {}", request.getId());
            return ResponseVo.fail(null, "没有数据插入");
        }
        return ResponseVo.success(null);
    }

    @Override
    public ResponseVo<Void> updateFunctionTask(FunctionTaskRequest request) {
        // check function task request
        if (checkFunctionGroovyRequest(request)) {
            return ResponseVo.fail(null, "缺少 task 信息");
        }
        LambdaQueryWrapper<YcFunctionTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionTask::getFunctionName, request.getFunctionName());
        wrapper.eq(YcFunctionTask::getTaskName, request.getTaskName());
        wrapper.eq(YcFunctionTask::getDelFlag, 0);
        YcFunctionTask functionTask = baseMapper.selectOne(wrapper);
        if (null == functionTask) {
            logger.warn("function task not exist, function = {}", request.getId());
            return ResponseVo.fail(null, "没有找到数据");
        }
        // insert
        functionTask.setScript(request.scriptJson());
        functionTask.setUpdateUser(request.getUserName());
        functionTask.setUpdateTime(LocalDateTime.now());
        int rows = baseMapper.updateById(functionTask);
        if (rows <= 0) {
            logger.warn("no data update from [yc_function_task], function = {}", request.getId());
            return ResponseVo.fail(null, "没有数据更新");
        }
        return ResponseVo.success(null);
    }

    @Override
    public ResponseVo<Void> deleteFunctionTask(String functionName, String taskName, String userName) {
        LambdaQueryWrapper<YcFunctionTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionTask::getFunctionName, functionName);
        wrapper.eq(YcFunctionTask::getTaskName, taskName);
        wrapper.eq(YcFunctionTask::getDelFlag, 0);
        YcFunctionTask functionTask = baseMapper.selectOne(wrapper);
        if (null == functionTask) {
            logger.warn("function task not exist, functionName = {} ,taskName = {}", functionName, taskName);
            return ResponseVo.fail(null, "没有找到数据");
        }
        // insert
        functionTask.setDelFlag(true);
        functionTask.setUpdateUser(userName);
        functionTask.setUpdateTime(LocalDateTime.now());
        int rows = baseMapper.updateById(functionTask);
        if (rows <= 0) {
            logger.warn("no data update from [yc_function_task], functionName = {} ,taskName = {}", functionName, taskName);
            return ResponseVo.fail(null, "没有数据删除");
        }
        return ResponseVo.success(null);
    }


    private static boolean checkFunctionGroovyRequest(FunctionTaskRequest request) {
        if (StrUtil.isEmptyIfStr(request.getFunctionName())
                || StrUtil.isEmptyIfStr(request.getTaskName())) {
            logger.error("function task is empty");
            return true;
        }
        return false;
    }


    private int insertFunctionTask(FunctionTaskRequest request) {
        YcFunctionTask functionTask = new YcFunctionTask();
        functionTask.setFunctionName(request.getFunctionName());
        functionTask.setTaskName(request.getTaskName());
        functionTask.setScript(request.scriptJson());
        functionTask.setCreateUser(request.getUserName());
        functionTask.setUpdateUser(request.getUserName());
        functionTask.setCreateTime(LocalDateTime.now());
        functionTask.setUpdateTime(functionTask.getCreateTime());
        return baseMapper.insert(functionTask);
    }

}
