package yzggy.yucong.action.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * yc function management
 * </p>
 *
 * @author yamath
 * @since 2023-07-11
 */
@TableName("yc_function_manage")
public class YcFunctionManage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

    /**
     * 0=action >0=deleted
     */
    private Boolean delFlag;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;

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

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "YcFunctionManage{" +
            "id = " + id +
            ", functionName = " + functionName +
            ", functionJson = " + functionJson +
            ", functionUse = " + functionUse +
            ", functionUid = " + functionUid +
            ", delFlag = " + delFlag +
            ", createUser = " + createUser +
            ", createTime = " + createTime +
            ", updateUser = " + updateUser +
            ", updateTime = " + updateTime +
        "}";
    }
}
