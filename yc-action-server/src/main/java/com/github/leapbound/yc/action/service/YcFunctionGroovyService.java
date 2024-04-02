package com.github.leapbound.yc.action.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import com.github.leapbound.yc.action.entities.YcFunctionGroovy;
import com.github.leapbound.yc.action.model.dto.YcFunctionGroovyDto;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionGroovySaveRequest;

/**
 * @author yamath
 * @since 2023/10/12 10:50
 */
public interface YcFunctionGroovyService extends IService<YcFunctionGroovy> {

    ResponseVo<Void> saveFunctionGroovy(FunctionGroovySaveRequest request);

    ResponseVo<Void> updateFunctionGroovy(FunctionGroovySaveRequest request);

    ResponseVo<Void> deleteFunctionGroovy(String functionName,
                                          String userName);

    YcFunctionGroovyDto getFunctionGroovyDto(String functionName);

    ResponseVo<Void> uploadFunctionGroovyScripts(MultipartFile file,
                                                 String groovyUrl);

    void checkFunctionGroovyScripts();
}
