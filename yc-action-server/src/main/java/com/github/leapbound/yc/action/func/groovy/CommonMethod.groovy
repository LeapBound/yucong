package com.github.leapbound.yc.action.func.groovy

import cn.hutool.extra.spring.SpringUtil
import com.github.leapbound.yc.action.service.YcFunctionOpenaiService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author yamath
 * @date 2024/7/10 15:56
 *
 */
class CommonMethod {
    static Logger logger = LoggerFactory.getLogger(CommonMethod.class)

    static YcFunctionOpenaiService ycFunctionOpenaiService = SpringUtil.getBean(YcFunctionOpenaiService.class)

    static def execCommonMethod(String scriptName, String method, Object params) {
        return ycFunctionOpenaiService.executeCommonScript(scriptName, method, params)
    }

    static def getExternalArgs() {
        return ycFunctionOpenaiService.getExternalArgs()
    }
}
