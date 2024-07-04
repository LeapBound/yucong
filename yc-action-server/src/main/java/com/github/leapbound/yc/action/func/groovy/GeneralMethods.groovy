package com.github.leapbound.yc.action.func.groovy

import com.alibaba.fastjson.JSONObject

/**
 *
 * @author yamath
 * @date 2024/5/9 15:31
 *
 */
class GeneralMethods {

    // Define a static method to create a response vo
    static def makeResponseVo(boolean success, String msg, Object data) {
        // return type as jsonObject
        return new JSONObject() {
            {
                put('success', success)
                put('code', null)
                put('msg', msg)
                put('data', data)
            }
        }
    }
}
