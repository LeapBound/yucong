package yzggy.yucong.action.model.vo;

import yzggy.yucong.action.model.vo.request.BaseRequest;

import java.io.Serializable;

/**
 * @author yamath
 * @since 2023/7/11 14:36
 */
public class YcFunctionRoleVo extends BaseRequest implements Serializable {

    private String roleId;

    private String functionName;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return "YcFunctionRoleVo{" +
                "roleId='" + roleId + '\'' +
                ", functionName='" + functionName + '\'' +
                '}';
    }
}
