package yzggy.yucong.action.model.vo.request;

import java.util.List;

/**
 * @author yamath
 * @since 2023/7/11 14:19
 */
public class FunctionRoleSaveRequest extends BaseRequest {

    private String roleId;

    private List<String> functionNameList;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public List<String> getFunctionNameList() {
        return functionNameList;
    }

    public void setFunctionNameList(List<String> functionNameList) {
        this.functionNameList = functionNameList;
    }

    @Override
    public String toString() {
        return "FunctionRoleSaveRequest{" +
                "roleId='" + roleId + '\'' +
                ", functionNameList=" + functionNameList +
                '}';
    }
}
