package geex.architecture.guts.camunda.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import geex.architecture.guts.camunda.entities.ProcessFunctionManage;
import geex.architecture.guts.camunda.mapper.ProcessFunctionManageMapper;
import geex.architecture.guts.camunda.model.bo.ProcessFunctionSaveRequest;
import geex.architecture.guts.camunda.model.vo.R;
import geex.architecture.guts.camunda.service.FunctionManageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yamath
 * @since 2023/11/24 11:46
 */
@Service
public class FunctionManageServiceImpl
        extends ServiceImpl<ProcessFunctionManageMapper, ProcessFunctionManage>
        implements FunctionManageService {

    private final static Integer IN_USE = 1;

    @Override
    public R<?> saveProcessFunctionManage(ProcessFunctionSaveRequest processFunctionSaveRequest) {
        String processKey = processFunctionSaveRequest.getProcessKey();
        String functionName = processFunctionSaveRequest.getFunctionName();
        String activityId = processFunctionSaveRequest.getActivityId();
        if (StrUtil.isEmptyIfStr(processKey) || StrUtil.isEmptyIfStr(activityId)) {
            return R.error(9911, "Save process function error, request cannot be null or empty");
        }
        LambdaQueryWrapper<ProcessFunctionManage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessFunctionManage::getProcessKey, processKey);
        queryWrapper.eq(ProcessFunctionManage::getActivityId, activityId);
        queryWrapper.eq(ProcessFunctionManage::getInUse, IN_USE);
        ProcessFunctionManage processFunctionManage = baseMapper.selectOne(queryWrapper);
        if (processFunctionManage == null) { // not found, then save
            processFunctionManage = new ProcessFunctionManage();
            processFunctionManage.setProcessKey(processFunctionSaveRequest.getProcessKey());
            processFunctionManage.setFunctionName(processFunctionSaveRequest.getFunctionName());
            processFunctionManage.setActivityId(processFunctionSaveRequest.getActivityId());
            processFunctionManage.setInUse(IN_USE);
            processFunctionManage.setCreateUser(processFunctionSaveRequest.getUsername());
            processFunctionManage.setCreateTime(LocalDateTime.now());
            int row = baseMapper.insert(processFunctionManage);
        } else {
            if (!processFunctionManage.getFunctionName().equals(functionName)) {
                processFunctionManage.setFunctionName(functionName);
                processFunctionManage.setUpdateTime(LocalDateTime.now());
                processFunctionManage.setUpdateUser(processFunctionSaveRequest.getUsername());
                int row = baseMapper.updateById(processFunctionManage);
            }
        }
        return R.ok(null);
    }

    @Override
    public R<?> getProcessFunction(String processKey) {
        LambdaQueryWrapper<ProcessFunctionManage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessFunctionManage::getProcessKey, processKey);
        queryWrapper.eq(ProcessFunctionManage::getInUse, IN_USE);
        List<ProcessFunctionManage> list = baseMapper.selectList(queryWrapper);
        return R.ok(list);
    }
}
