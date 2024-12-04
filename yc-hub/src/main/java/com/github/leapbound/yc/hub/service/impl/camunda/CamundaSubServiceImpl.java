package com.github.leapbound.yc.hub.service.impl.camunda;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.camunda.handler.ServiceTaskSubService;
import org.springframework.stereotype.Service;

/**
 * @author yamath
 * @date 2024/4/7 10:45
 */
@Service
public class CamundaSubServiceImpl implements ServiceTaskSubService {


    @Override
    public JSONObject execute(String method, String arguments) throws Exception {
        return new JSONObject();
    }
}
