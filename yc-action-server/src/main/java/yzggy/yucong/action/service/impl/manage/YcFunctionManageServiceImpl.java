package yzggy.yucong.action.service.impl.manage;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yzggy.yucong.action.entities.YcFunctionManage;
import yzggy.yucong.action.mapper.YcFunctionManageMapper;
import yzggy.yucong.action.model.dto.YcFunctionManageDto;
import yzggy.yucong.action.model.vo.ResponseVo;
import yzggy.yucong.action.model.vo.request.FunctionMethodSaveRequest;
import yzggy.yucong.action.service.YcFunctionManageService;
import yzggy.yucong.action.service.YcFunctionMethodService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yamath
 * @since 2023/7/11 10:15
 */
@Service
public class YcFunctionManageServiceImpl
        extends ServiceImpl<YcFunctionManageMapper, YcFunctionManage>
        implements YcFunctionManageService {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionManageServiceImpl.class);

    private final YcFunctionMethodService ycFunctionMethodService;

    public YcFunctionManageServiceImpl(YcFunctionMethodService ycFunctionMethodService) {
        this.ycFunctionMethodService = ycFunctionMethodService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo<Void> saveFunctionManage(FunctionMethodSaveRequest request) {
        if (!this.checkApiFunctionRequest(request)) {
            return ResponseVo.fail(null, "functions 参数不符合要求");
        }
        LambdaQueryWrapper<YcFunctionManage> wrapper = getFunctionManageByNameQueryWrapper(request.getFunctionName());
        YcFunctionManage ycFunctionManage = baseMapper.selectOne(wrapper);
        // exist
        if (null != ycFunctionManage) {
            logger.warn("function has exist, function = {}", request.getFunctionName());
            return ResponseVo.fail(null, "functions.name 已经存在");
        }
        // not exist
        // set function manage
        ycFunctionManage = this.setFunctionManage(ycFunctionManage, request);
        // insert function manage
        int row = this.baseMapper.insert(ycFunctionManage);
        if (row <= 0) {
            logger.warn("no data insert into [yc_function_manage], function = {}", request.getFunctionName());
            return ResponseVo.fail(null, "没有数据插入");
        }
        // insert function method
        if (!StrUtil.isEmptyIfStr(request.getFunctionClass()) &&
                !StrUtil.isEmptyIfStr(request.getFunctionMethod())) {
            request.setId(ycFunctionManage.getId());
            return this.ycFunctionMethodService.saveFunctionMethod(request);
        }
        return ResponseVo.success(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo<Void> updateFunctionManage(FunctionMethodSaveRequest request) {
        // check
        if (!this.checkApiFunctionRequest(request)) {
            return ResponseVo.fail(null, "functions 参数不符合要求");
        }
        // select function
        LambdaQueryWrapper<YcFunctionManage> wrapper = getFunctionManageByNameQueryWrapper(request.getFunctionName());
        YcFunctionManage ycFunctionManage = baseMapper.selectOne(wrapper);
        if (null == ycFunctionManage) {
            logger.error("no data found in [yc_function_manage], function = {}", request.getFunctionName());
            return ResponseVo.fail(null, "没有找到数据");
        }
        //
        ycFunctionManage = this.setFunctionManage(ycFunctionManage, request);
        // update function manage
        int row = baseMapper.updateById(ycFunctionManage);
        if (row <= 0) {
            logger.warn("no data update from [yc_function_manage], function = {}", request.getFunctionName());
            return ResponseVo.fail(null, "没有数据更新");
        }
        // update function method
        if (!StrUtil.isEmptyIfStr(request.getFunctionClass()) &&
                !StrUtil.isEmptyIfStr(request.getFunctionMethod())) {
            request.setId(ycFunctionManage.getId());
            return this.ycFunctionMethodService.updateFunctionMethod(request);
        }
        return ResponseVo.success(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo<Void> deleteFunctionManage(String functionName,
                                                 String userName) {
        // select function
        LambdaQueryWrapper<YcFunctionManage> wrapper = getFunctionManageByNameQueryWrapper(functionName);
        YcFunctionManage ycFunctionManage = baseMapper.selectOne(wrapper);
        // if not exist
        if (null == ycFunctionManage) {
            logger.error("no data found in [yc_function_manage], function = {}", functionName);
            return ResponseVo.fail(null, "没有找到数据");
        }
        // delete
        ycFunctionManage.setDelFlag(true);
        ycFunctionManage.setUpdateUser(userName);
        ycFunctionManage.setUpdateTime(LocalDateTime.now());
        int row = baseMapper.updateById(ycFunctionManage);
        if (row <= 0) {
            logger.warn("no data delete from [yc_function_manage], function = {}", functionName);
            return ResponseVo.fail(null, "没有数据删除");
        }
        // delete from yc_function_method
        return this.ycFunctionMethodService.deleteFunctionMethod(ycFunctionManage.getFunctionName(), userName);
    }

    @Override
    public List<YcFunctionManageDto> getFunctionManageDtoList(List<String> roleIdList) {
        return baseMapper.selectFunctionManageByRole(roleIdList);
    }

    private static LambdaQueryWrapper<YcFunctionManage> getFunctionManageByNameQueryWrapper(String functionName) {
        LambdaQueryWrapper<YcFunctionManage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionManage::getFunctionName, functionName);
        wrapper.eq(YcFunctionManage::getDelFlag, 0);
        return wrapper;
    }

    /**
     * check functions request
     * - check functions name exist
     * - check functions json exist
     *
     * @param request functions 请求
     * @return true or false
     */
    private Boolean checkApiFunctionRequest(FunctionMethodSaveRequest request) {
        if (StrUtil.isEmptyIfStr(request.getFunctionName())) {
            logger.error("functions.name is empty, name = {}", request.getFunctionName());
            return false;
        }
        if (null == request.getFunctionJson() || request.getFunctionJson().isEmpty()) {
            logger.error("functions.json is empty, name = {}", request.getFunctionName());
            return false;
        }
        return true;
    }

    /**
     * set function manage
     *
     * @param ycFunctionManage mapper ApiFunctionManage
     * @param request          functions request
     * @return mapper ApiFunctionManage
     */
    private YcFunctionManage setFunctionManage(YcFunctionManage ycFunctionManage, FunctionMethodSaveRequest request) {
        if (ycFunctionManage == null) {
            ycFunctionManage = new YcFunctionManage();
            ycFunctionManage.setFunctionUid(setFunctionUid(request.getFunctionName())); // functions uid
            ycFunctionManage.setFunctionName(request.getFunctionName()); // functions name
            ycFunctionManage.setFunctionJson(JSON.toJSONString(request.getFunctionJson())); // functions
            ycFunctionManage.setFunctionUse(true);
            ycFunctionManage.setDelFlag(false);
            ycFunctionManage.setCreateUser(request.getUserName());
            ycFunctionManage.setCreateTime(LocalDateTime.now());
        } else {
            ycFunctionManage.setFunctionJson(JSON.toJSONString(request.getFunctionJson())); // functions json
            if (request.getFunctionUse() != null) {
                ycFunctionManage.setFunctionUse(1 == request.getFunctionUse());
            }
            ycFunctionManage.setUpdateUser(request.getUserName());
            ycFunctionManage.setUpdateTime(LocalDateTime.now());
        }
        return ycFunctionManage;
    }

    /**
     * set function uid
     *
     * @param functionName function name
     * @return function uid
     */
    private static String setFunctionUid(String functionName) {
        return DigestUtil.md5Hex(functionName);
    }
}
