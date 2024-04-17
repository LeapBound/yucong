package com.github.leapbound.yc.action.service.impl.camunda;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.action.service.YcFunctionOpenaiService;
import com.github.leapbound.yc.camunda.handler.ServiceTaskSubService;
import org.springframework.stereotype.Service;

/**
 * @author yamath
 * @since 2024/4/7 10:45
 */
@Service
public class CamundaSubServiceImpl implements ServiceTaskSubService {

    private final YcFunctionOpenaiService ycFunctionOpenaiService;

    public CamundaSubServiceImpl(YcFunctionOpenaiService ycFunctionOpenaiService) {
        this.ycFunctionOpenaiService = ycFunctionOpenaiService;
    }

    @Override
    public JSONObject execute(String method, String arguments) throws Exception {
        return ycFunctionOpenaiService.executeGroovy(method, arguments);
    }
}
