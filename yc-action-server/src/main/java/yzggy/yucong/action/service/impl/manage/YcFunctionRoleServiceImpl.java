package yzggy.yucong.action.service.impl.manage;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yzggy.yucong.action.entities.YcFunctionRole;
import yzggy.yucong.action.mapper.YcFunctionRoleMapper;
import yzggy.yucong.action.model.vo.ResponseVo;
import yzggy.yucong.action.model.vo.YcFunctionRoleVo;
import yzggy.yucong.action.model.vo.request.FunctionRoleSaveRequest;
import yzggy.yucong.action.service.YcFunctionRoleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yamath
 * @since 2023/7/11 10:14
 */
@Service
public class YcFunctionRoleServiceImpl
        extends ServiceImpl<YcFunctionRoleMapper, YcFunctionRole>
        implements YcFunctionRoleService {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionRoleServiceImpl.class);

    @Override
    public ResponseVo<Void> saveFunctionRole(FunctionRoleSaveRequest request) {
        LambdaQueryWrapper<YcFunctionRole> wrapper = getFunctionRoleQueryWrapper(request.getRoleId(), null);
        List<YcFunctionRole> ycFunctionRoleList = baseMapper.selectList(wrapper);
        // insert function role list
        // if not exist, all insert; if part exist, part insert
        int rows = this.insertFunctionRoleList(ycFunctionRoleList, request);
        if (rows <= 0) {
            logger.warn("no data insert into [yc_function_role], role = {}", request.getRoleId());
            return ResponseVo.fail(null, "没有数据插入");
        }
        //
        return ResponseVo.success(null);
    }

    @Override
    public ResponseVo<Void> deleteFunctionRole(YcFunctionRoleVo vo) {
        LambdaQueryWrapper<YcFunctionRole> wrapper = getFunctionRoleQueryWrapper(vo.getRoleId(), vo.getFunctionName());
        YcFunctionRole ycFunctionRole = baseMapper.selectOne(wrapper);
        if (ycFunctionRole == null) {
            logger.error("no data found in [yc_function_role], role = {}, function = {}", vo.getRoleId(), vo.getFunctionName());
            return ResponseVo.fail(null, "没有找到数据");
        }
        ycFunctionRole.setDelFlag(true);
        ycFunctionRole.setUpdateUser(vo.getUserName());
        ycFunctionRole.setUpdateTime(LocalDateTime.now());
        int row = baseMapper.updateById(ycFunctionRole);
        if (row <= 0) {
            logger.warn("no data delete from [yc_function_role], role = {}, function = {}", vo.getRoleId(), vo.getFunctionName());
            return ResponseVo.fail(null, "没有数据删除");
        }
        return ResponseVo.success(null);
    }

    @Override
    public List<String> getAllFunctionRoleIdList() {
        return baseMapper.selectFunctionRoleIdList();
    }

    private static LambdaQueryWrapper<YcFunctionRole> getFunctionRoleQueryWrapper(String roleId, String functionName) {
        LambdaQueryWrapper<YcFunctionRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionRole::getRoleId, roleId);
        if (!StrUtil.isEmptyIfStr(functionName)) {
            wrapper.eq(YcFunctionRole::getFunctionName, functionName);
        }
        wrapper.eq(YcFunctionRole::getDelFlag, 0);
        return wrapper;
    }

    private int insertFunctionRoleList(List<YcFunctionRole> list, FunctionRoleSaveRequest request) {
        List<YcFunctionRole> insertList = new ArrayList<>();
        for (String functionName : request.getFunctionNameList()) {
            if (list != null && !list.isEmpty()) {
                boolean exist = list.stream().anyMatch(ycFunctionRole -> ycFunctionRole.getFunctionName().equals(functionName));
                if (exist) {
                    continue;
                }
            }
            // todo check function manage exist
            YcFunctionRole ycFunctionRole = new YcFunctionRole();
            ycFunctionRole.setRoleId(request.getRoleId());
            ycFunctionRole.setFunctionName(functionName);
            ycFunctionRole.setDelFlag(false);
            ycFunctionRole.setCreateUser(request.getUserName());
            ycFunctionRole.setCreateTime(LocalDateTime.now());
            insertList.add(ycFunctionRole);
        }
        //
        if (!insertList.isEmpty()) {
            return baseMapper.insertBatchSomeColumn(insertList);
        }
        return 0;
    }
}
