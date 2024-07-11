package com.github.leapbound.yc.action.func.groovy

import com.alibaba.fastjson.JSONObject

/**
 *
 * @author yamath
 * @since 2024/5/24 17:37
 *
 */
class ResponseVo {
    boolean success
    Integer code
    String msg
    Object data

    ResponseVo() {}

    ResponseVo(boolean success, Integer code, String msg, Object data) {
        this.success = success
        this.code = code
        this.msg = msg
        this.data = data
    }

    ResponseVo success(Object data) {
        return ResponseVo(true, null, '', data)
    }

    ResponseVo fail(Integer code, String msg) {
        return ResponseVo(false, code, '', null)
    }

    static JSONObject makeSuccess(Object data) {
        return new JSONObject() {
            {
                put('success', true)
                put('code', null)
                put('msg', '')
                put('data', data)
            }
        }
    }

    static JSONObject makeFail(Integer code, String msg) {
        return new JSONObject() {
            {
                put('success', false)
                put('code', code)
                put('msg', msg)
                put('data', null)
            }
        }
    }

    static JSONObject makeResponse(com.github.leapbound.yc.action.model.vo.ResponseVo response) {
        return new JSONObject() {
            {
                put('success', response.success)
                put('code', response.code)
                put('msg', response.msg)
                put('data', response.data)
            }
        }
    }
}
