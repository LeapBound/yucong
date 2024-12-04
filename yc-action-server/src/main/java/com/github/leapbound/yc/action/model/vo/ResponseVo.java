package com.github.leapbound.yc.action.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yamath
 * @date 2023/7/3 15:00
 */
public class ResponseVo<T> implements Serializable {

    private boolean success;

    private String code;

    private String msg;

    private T data;

    public ResponseVo() {
    }

    public ResponseVo(boolean success, String code, String msg, T data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<>(true, null, null, data);
    }

    public static <T> ResponseVo<T> success(String code, String msg, T data) {
        return new ResponseVo<>(true, code, msg, data);
    }

    public static <T> ResponseVo<T> fail(String code, String msg) {
        return new ResponseVo<>(false, code, msg, null);
    }

    public static <T> ResponseVo<T> fail(String code, String msg, T data) {
        return new ResponseVo<>(false, code, msg, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseVo{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
