package com.github.leapbound.yc.action.model.vo.request;

import com.alibaba.fastjson.JSONObject;

/**
 * @author yamath
 * @date 2023/7/11 17:31
 */
public class FunctionMethodSaveRequest extends BaseRequest {

    private Integer id;

    private String functionName;

    private JSONObject functionJson;

    private Integer functionUse;

    private String functionUid;

    private String functionClass;

    private String functionMethod;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public JSONObject getFunctionJson() {
        return functionJson;
    }

    public void setFunctionJson(JSONObject functionJson) {
        this.functionJson = functionJson;
    }

    public Integer getFunctionUse() {
        return functionUse;
    }

    public void setFunctionUse(Integer functionUse) {
        this.functionUse = functionUse;
    }

    public String getFunctionUid() {
        return functionUid;
    }

    public void setFunctionUid(String functionUid) {
        this.functionUid = functionUid;
    }

    public String getFunctionClass() {
        return functionClass;
    }

    public void setFunctionClass(String functionClass) {
        this.functionClass = functionClass;
    }

    public String getFunctionMethod() {
        return functionMethod;
    }

    public void setFunctionMethod(String functionMethod) {
        this.functionMethod = functionMethod;
    }

    @Override
    public String toString() {
        return "FunctionSaveRequest{" +
                "id=" + id +
                ", functionName='" + functionName + '\'' +
                ", functionJson=" + functionJson +
                ", functionUse=" + functionUse +
                ", functionUid='" + functionUid + '\'' +
                ", functionClass='" + functionClass + '\'' +
                ", functionMethod='" + functionMethod + '\'' +
                '}';
    }
}
