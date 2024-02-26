package com.github.leapbound.yc.action.model.dto;

/**
 * @author yamath
 * @since 2023/7/12 10:56
 */
public class YcFunctionMethodDto {

    private Integer functionId;

    private String functionName;

    private String functionClass;

    private String functionMethod;

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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
}
