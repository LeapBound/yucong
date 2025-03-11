package com.github.leapbound.yc.hub.service.impl.camunda;

import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.camunda.handler.ServiceTaskSubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yamath
 * @date 2024/4/7 10:45
 */
@Slf4j
@Service
public class ServiceTaskSubServiceImpl implements ServiceTaskSubService {

    @Override
    public JSONObject execute(String method, String arguments) throws Exception {
        log.info("camunda sub service {} {}", method, arguments);
        return new JSONObject();
    }
}
