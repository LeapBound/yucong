package yzggy.yucong.action.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * function methods
 * </p>
 *
 * @author yamath
 * @since 2023-07-11
 */
@TableName("yc_function_method")
public class YcFunctionMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * function name
     */
    private String functionName;

    /**
     * function class package
     */
    private String functionClass;

    /**
     * function method
     */
    private String functionMethod;

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
        return "YcFunctionMethod{" +
            "id = " + id +
            ", functionName = " + functionName +
            ", functionClass = " + functionClass +
            ", functionMethod = " + functionMethod +
            ", delFlag = " + delFlag +
            ", createUser = " + createUser +
            ", createTime = " + createTime +
            ", updateUser = " + updateUser +
            ", updateTime = " + updateTime +
        "}";
    }
}
