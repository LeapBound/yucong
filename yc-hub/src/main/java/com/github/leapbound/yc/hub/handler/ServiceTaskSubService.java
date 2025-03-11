package com.github.leapbound.yc.hub.handler;


import com.alibaba.fastjson.JSONObject;

/**
 * @author yamath
 * @date 2024/4/7 10:09
 */
public interface ServiceTaskSubService {

    JSONObject execute(String method, String arguments) throws Exception;
}
