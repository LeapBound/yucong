package com.github.leapbound.yc.action.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.action.model.dto.YcFunctionGroovyDto;
import com.google.common.collect.Maps;
import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/10/9 11:13
 */
public class FunctionGroovyExec {

    private static final Logger logger = LoggerFactory.getLogger(FunctionGroovyExec.class);

    public static GroovyScriptEngine createGroovyEngine(String groovyUrl) throws Exception {
        try {
            return new GroovyScriptEngine(groovyUrl);
        } catch (Exception ex) {
            logger.error("create groovy engine error, groovyUrl: {}", groovyUrl, ex);
            throw new Exception(ex);
        }
    }

    public static JSONObject runScript(GroovyScriptEngine engine, YcFunctionGroovyDto functionGroovyDto, JSONObject arguments) throws Exception {
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
            return null;
        } catch (Exception ex) {
            logger.error("execute groovy script error,", ex);
            throw new Exception(ex);
        }
    }

    public static Object runScriptMethod(GroovyScriptEngine engine, String groovyName, String method, Object arguments) throws Exception {
        try {
            // common script has no bindings
            Script script = engine.createScript(groovyName, new Binding());
            // invoke method
            return script.invokeMethod(method, arguments);
        } catch (Exception ex) {
            logger.error("execute groovy script error,", ex);
            throw new Exception(ex);
        }
    }
}
