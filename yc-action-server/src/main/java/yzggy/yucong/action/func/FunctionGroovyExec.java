package yzggy.yucong.action.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yzggy.yucong.action.model.dto.YcFunctionGroovyDto;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/10/9 11:13
 */
public class FunctionGroovyExec {

    private static final Logger logger = LoggerFactory.getLogger(FunctionGroovyExec.class);

    public static JSONObject executeGroovy(YcFunctionGroovyDto functionGroovyDto, JSONObject arguments) {
        String groovyUrl = functionGroovyDto.getGroovyUrl();
        String groovyName = functionGroovyDto.getGroovyName();
        try {
            // groovy script engine
            GroovyScriptEngine engine = new GroovyScriptEngine(groovyUrl);
            // param
            Map<String, String> map = Maps.newHashMap();
            map.put("method", functionGroovyDto.getFunctionName());
            map.put("arguments", arguments.toJSONString());
            Binding binding = new Binding(map);
            // groovy result
            Object result = engine.run(groovyName, binding);

            if (result != null) {
                return (JSONObject) JSON.toJSON(result);
            }
        } catch (Exception ex) {
            logger.error("execute groovy script error,", ex);
        }
        return null;
    }
}
