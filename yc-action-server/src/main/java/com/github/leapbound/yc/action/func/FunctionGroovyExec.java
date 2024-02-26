package com.github.leapbound.yc.action.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.leapbound.yc.action.model.dto.YcFunctionGroovyDto;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/10/9 11:13
 */
public class FunctionGroovyExec {

    private static final Logger logger = LoggerFactory.getLogger(FunctionGroovyExec.class);

    public static GroovyScriptEngine createGroovyEngine(String groovyUrl) {
        try {
            return new GroovyScriptEngine(groovyUrl);
        } catch (Exception ex) {
            logger.error("create groovy engine error, groovyUrl: {}", groovyUrl, ex);
        }
        return null;
    }

    public static JSONObject runScript(GroovyScriptEngine engine, YcFunctionGroovyDto functionGroovyDto, JSONObject arguments) {
        String groovyName = functionGroovyDto.getGroovyName();
        // param
        try {
            Map<String, String> map = Maps.newHashMap();
            map.put("method", functionGroovyDto.getFunctionName());
            map.put("arguments", arguments.toJSONString());
            Binding binding = new Binding(map);

            Object object = engine.run(groovyName, binding);
            if (object != null) {
                return (JSONObject) JSON.toJSON(object);
            }
        } catch (Exception ex) {
            logger.error("execute groovy script error,", ex);
        }
        return null;
    }
}
