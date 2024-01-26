package yzggy.yucong.action.model.dto;

import java.io.Serializable;

/**
 * @author yamath
 * @since 2023/7/11 10:02
 */
public class YcFunctionManageDto implements Serializable {

    private Integer id;

    /**
     * function name
     */
    private String functionName;

    /**
     * openai function json
     */
    private String functionJson;

    /**
     * 1=using 0=not
     */
    private Boolean functionUse;

    /**
     * function uid
     */
    private String functionUid;

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

    public String getFunctionJson() {
        return functionJson;
    }

    public void setFunctionJson(String functionJson) {
        this.functionJson = functionJson;
    }

    public Boolean getFunctionUse() {
        return functionUse;
    }

    public void setFunctionUse(Boolean functionUse) {
        this.functionUse = functionUse;
    }

    public String getFunctionUid() {
        return functionUid;
    }

    public void setFunctionUid(String functionUid) {
        this.functionUid = functionUid;
    }

    @Override
    public String toString() {
        return "YcFunctionManageDto{" +
                "id=" + id +
                ", functionName='" + functionName + '\'' +
                ", functionJson='" + functionJson + '\'' +
                ", functionUse=" + functionUse +
                ", functionUid='" + functionUid + '\'' +
                '}';
    }
}
