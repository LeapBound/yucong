package com.github.leapbound.yc.camunda.model.vo;

import org.springframework.http.HttpStatus;

/**
 * @author yamath
 * @date 2023/11/16 11:38
 */
public class R<T> {

    private Integer code;

    private T data;

    private String msg;

    public R() {
    }

    public R(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> boolean isOk(R<T> ret) {
        return ret.code == HttpStatus.OK.value();
    }

    public static <T> R<T> ok(T data) {
        return new R<T>(HttpStatus.OK.value(), data, HttpStatus.OK.getReasonPhrase());
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R<T>(HttpStatus.OK.value(), data, msg);
    }

    public static <T> R<T> error(HttpStatus httpStatus) {
        return new R<T>(httpStatus.value(), null, httpStatus.getReasonPhrase());
    }

    public static <T> R<T> error(Integer code, String msg) {
        R<T> r = new R<T>();
        r.code(code);
        r.data(null);
        r.msg(msg);
        return r;
    }

    public Integer getCode() {
        return code;
    }

    public R<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public R<T> msg(String msg) {
        this.msg = msg;
        return this;
    }
}
