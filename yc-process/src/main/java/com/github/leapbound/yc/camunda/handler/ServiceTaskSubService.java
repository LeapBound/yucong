package com.github.leapbound.yc.camunda.handler;


import com.alibaba.fastjson.JSONObject;

/**
 * @author yamath
 * @since 2024/4/7 10:09
 */
public interface ServiceTaskSubService {

    JSONObject execute(String method, String arguments) throws Exception;
}
