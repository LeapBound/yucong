package yzggy.yucong.action.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author yamath
 * @since 2023-10-12
 */
@TableName("yc_function_groovy")
public class YcFunctionGroovy implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 方法名
     */
    private String functionName;

    /**
     * groovy 脚本名
     */
    private String groovyName;

    /**
     * groovy 脚本地址
     */
    private String groovyUrl;

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

    public String getGroovyName() {
        return groovyName;
    }

    public void setGroovyName(String groovyName) {
        this.groovyName = groovyName;
    }

    public String getGroovyUrl() {
        return groovyUrl;
    }

    public void setGroovyUrl(String groovyUrl) {
        this.groovyUrl = groovyUrl;
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
        return "YcFunctionGroovy{" +
                "id=" + id +
                ", functionName='" + functionName + '\'' +
                ", groovyName='" + groovyName + '\'' +
                ", groovyUrl='" + groovyUrl + '\'' +
                ", delFlag=" + delFlag +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", updateUser='" + updateUser + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
