package com.github.leapbound.yc.action.model.vo.request;

/**
 * @author yamath
 * @since 2023/7/13 15:36
 */
public class FunctionExecuteRequest extends BaseRequest {

    private String name;

    private String arguments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "FunctionExecuteRequest{" +
                "name='" + name + '\'' +
                ", arguments='" + arguments + '\'' +
                '}';
    }
}
