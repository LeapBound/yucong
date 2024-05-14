package com.github.leapbound.yc.action.service.impl.manage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.leapbound.yc.action.entities.YcFunctionGroovy;
import com.github.leapbound.yc.action.mapper.YcFunctionGroovyMapper;
import com.github.leapbound.yc.action.model.dto.YcFunctionGroovyDto;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionGroovySaveRequest;
import com.github.leapbound.yc.action.service.YcFunctionGroovyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yamath
 * @since 2023/10/12 10:53
 */
@Service
public class YcFunctionGroovyServiceImpl
        extends ServiceImpl<YcFunctionGroovyMapper, YcFunctionGroovy>
        implements YcFunctionGroovyService {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionGroovyServiceImpl.class);

    @Override
    public ResponseVo<Void> saveFunctionGroovy(FunctionGroovySaveRequest request) {
        // check function method request
        if (checkFunctionGroovyRequest(request)) {
            return ResponseVo.fail(null, "缺少 groovy 信息");
        }
        // select function method
        LambdaQueryWrapper<YcFunctionGroovy> wrapper = getFunctionGroovyQueryWrapper(request.getFunctionName());
        YcFunctionGroovy ycFunctionGroovy = baseMapper.selectOne(wrapper);
        // if exist
        if (ycFunctionGroovy != null) {
            logger.warn("function groovy exist, function = {}", request.getId());
            return ResponseVo.fail(null, "数据已经存在");
        }
        // insert
        int rows = this.insertFunctionGroovy(request);
        if (rows <= 0) {
            logger.warn("no data insert into [yc_function_groovy], function = {}", request.getId());
            return ResponseVo.fail(null, "没有数据插入");
        }
        return ResponseVo.success(null);
    }

    @Override
    public ResponseVo<Void> updateFunctionGroovy(FunctionGroovySaveRequest request) {
        // check function method request
        if (checkFunctionGroovyRequest(request)) {
            return ResponseVo.fail(null, "缺少 method 信息");
        }
        // select function method
        LambdaQueryWrapper<YcFunctionGroovy> wrapper = getFunctionGroovyQueryWrapper(request.getFunctionName());
        YcFunctionGroovy ycFunctionGroovy = baseMapper.selectOne(wrapper);
        // if not exist
        if (ycFunctionGroovy == null) {
            logger.error("function groovy not exist, function = {}", request.getFunctionName());
            return ResponseVo.fail(null, "没有找到数据");
        }
        // update
        ycFunctionGroovy.setGroovyName(request.getGroovyName());
        ycFunctionGroovy.setGroovyUrl(request.getGroovyUrl());
        ycFunctionGroovy.setUpdateUser(request.getUserName());
        ycFunctionGroovy.setUpdateTime(LocalDateTime.now());
        int row = baseMapper.updateById(ycFunctionGroovy);
        if (row <= 0) {
            logger.warn("no data update from [yc_function_groovy], function = {}", request.getId());
            return ResponseVo.fail(null, "没有数据更新");
        }
        return ResponseVo.success(null);
    }

    @Override
    public ResponseVo<Void> deleteFunctionGroovy(String functionName, String userName) {
        // select function method
        LambdaQueryWrapper<YcFunctionGroovy> wrapper = getFunctionGroovyQueryWrapper(functionName);
        YcFunctionGroovy ycFunctionGroovy = baseMapper.selectOne(wrapper);
        // if not exist
        if (ycFunctionGroovy == null) {
            logger.error("function groovy not exist, function = {}", functionName);
            return ResponseVo.fail(null, "没有找到数据");
        }
        // delete
        ycFunctionGroovy.setDelFlag(true);
        ycFunctionGroovy.setUpdateUser(userName);
        ycFunctionGroovy.setUpdateTime(LocalDateTime.now());
        int row = baseMapper.updateById(ycFunctionGroovy);
        if (row <= 0) {
            logger.warn("no data delete from [yc_function_groovy], function = {}", functionName);
            return ResponseVo.fail(null, "没有数据删除");
        }
        return ResponseVo.success(null);
    }

    @Override
    public YcFunctionGroovyDto getFunctionGroovyDto(String functionName) {
        return baseMapper.selectFunctionGroovyDtoByName(functionName);
    }

    @Override
    public ResponseVo<Void> uploadFunctionGroovyScripts(MultipartFile file, String groovyUrl) {
        //
        groovyUrl = checkUrl(groovyUrl);
        try {
            String groovyName = file.getOriginalFilename();
            YcFunctionGroovyDto dto = baseMapper.selectFunctionGroovyDtoByGroovy(groovyName);
            if (dto != null) {
                if (groovyUrl.equals(dto.getGroovyUrl())) {

                    if (!FileUtil.exist(groovyUrl)) {
                        FileUtil.mkdir(groovyUrl);
                    }
                    //
                    File groovy = new File(groovyUrl + file.getOriginalFilename());

                    file.transferTo(groovy.getAbsoluteFile());
                } else {
                    logger.error("Groovy scripts not match, {} - params {}", dto.getGroovyUrl(), groovyUrl);
                }
            } else {
                logger.error("Groovy scripts not exist in yc_function_groovy, {}", groovyName);
            }
            return ResponseVo.success(null);
        } catch (Exception ex) {
            logger.error("Groovy scripts upload error,", ex);
            return ResponseVo.fail(null, "upload Groovy scripts 失败");
        }
    }

    @Override
    public void checkFunctionGroovyScripts() {
        // check scripts in resources
        List<YcFunctionGroovyDto> list = baseMapper.selectFunctionGroovyDtoListByGroovy();
        if (list != null && !list.isEmpty()) {
            for (YcFunctionGroovyDto dto : list) {
                String groovyPath = dto.getGroovyUrl() + dto.getGroovyName(); // dest path
                // '/home' is the working directory
                String resourcePath = groovyPath.replaceFirst("/home/", "");
                try {
                    ClassPathResource cpr = new ClassPathResource(resourcePath);
                    InputStream is = cpr.getStream();
                    FileUtil.writeFromStream(is, groovyPath);
                } catch (Exception ex) {
                    logger.error("groovy script {} copy error, {}", dto.getGroovyName(), ex.getMessage());
                }
            }
        }
    }

    private static boolean checkFunctionGroovyRequest(FunctionGroovySaveRequest request) {
        if (StrUtil.isEmptyIfStr(request.getGroovyName())
                || StrUtil.isEmptyIfStr(request.getGroovyUrl())) {
            logger.error("function groovy is empty");
            return true;
        }
        return false;
    }

    private static LambdaQueryWrapper<YcFunctionGroovy> getFunctionGroovyQueryWrapper(String functionName) {
        LambdaQueryWrapper<YcFunctionGroovy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YcFunctionGroovy::getFunctionName, functionName);
        wrapper.eq(YcFunctionGroovy::getDelFlag, 0);
        return wrapper;
    }

    private int insertFunctionGroovy(FunctionGroovySaveRequest request) {
        YcFunctionGroovy ycFunctionGroovy = new YcFunctionGroovy();
        ycFunctionGroovy.setFunctionName(request.getFunctionName());
        ycFunctionGroovy.setGroovyName(request.getGroovyName());
        String groovyUrl = checkUrl(request.getGroovyUrl());
        ycFunctionGroovy.setGroovyUrl(groovyUrl);
        ycFunctionGroovy.setCreateUser(request.getUserName());
        ycFunctionGroovy.setCreateTime(LocalDateTime.now());
        return baseMapper.insert(ycFunctionGroovy);
    }

    private static String checkUrl(String groovyUrl) {
        groovyUrl = groovyUrl.startsWith("/") ? groovyUrl : "/" + groovyUrl;
        groovyUrl = groovyUrl.endsWith("/") ? groovyUrl : groovyUrl + "/";
        return groovyUrl;
    }
}
