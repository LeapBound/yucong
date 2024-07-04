package com.github.leapbound.yc.action.func;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.leapbound.yc.action.model.dto.YcFunctionMethodDto;
import com.github.leapbound.yc.action.utils.classloader.SpringInjectService;

import java.lang.reflect.Method;

/**
 * @author yamath
 * @date 2023/7/12 10:54
 */
@Deprecated
public class FunctionExecutor {

    private static final Logger logger = LoggerFactory.getLogger(FunctionExecutor.class);

    /**
     * execute methods in reflect method
     *
     * @param functionMethodDto function method
     * @param argument          method arguments
     * @return
     */
    public static JSONObject execute(YcFunctionMethodDto functionMethodDto, JSONObject argument) {
        String clazzName = functionMethodDto.getFunctionClass();
        String methodName = functionMethodDto.getFunctionMethod();
        JSONObject json = null;
        try {
            Class<?> clazz = Class.forName(clazzName);
            Method method = clazz.getMethod(methodName, JSONObject.class);
            json = (JSONObject) method.invoke(SpringInjectService.getBean(clazz), argument);
        } catch (Exception ex) {
            logger.error("function execute error, function = {}", functionMethodDto.getFunctionName(), ex);
        }
        return json;
    }
}
