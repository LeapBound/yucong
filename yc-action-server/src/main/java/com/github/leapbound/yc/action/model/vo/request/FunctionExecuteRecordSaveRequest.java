package com.github.leapbound.yc.action.model.vo.request;

/**
 * @author yamath
 * @since 2023/7/14 9:43
 */
public class FunctionExecuteRecordSaveRequest {

    private String functionName;

    private String executeArguments;

    private String executeUser;

    private String executeTime;

    private String resultTime;

    private long executeDuration;

    private String executeResult;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getExecuteArguments() {
        return executeArguments;
    }

    public void setExecuteArguments(String executeArguments) {
        this.executeArguments = executeArguments;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public String getResultTime() {
        return resultTime;
    }

    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    public long getExecuteDuration() {
        return executeDuration;
    }

    public void setExecuteDuration(long executeDuration) {
        this.executeDuration = executeDuration;
    }

    public String getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }

    @Override
    public String toString() {
        return "FunctionExecuteRecordSaveRequest{" +
                "functionName='" + functionName + '\'' +
                ", executeArguments='" + executeArguments + '\'' +
                ", executeUser='" + executeUser + '\'' +
                ", executeTime=" + executeTime +
                ", resultTime=" + resultTime +
                ", executeDuration=" + executeDuration +
                ", executeResult='" + executeResult + '\'' +
                '}';
    }
}
