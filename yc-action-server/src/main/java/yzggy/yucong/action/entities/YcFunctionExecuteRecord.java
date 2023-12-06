package yzggy.yucong.action.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * function execute record
 * </p>
 *
 * @author yamath
 * @since 2023-07-14
 */
@TableName("yc_function_execute_record")
public class YcFunctionExecuteRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * function name
     */
    private String functionName;

    /**
     * arguments
     */
    private String executeArguments;

    /**
     * user
     */
    private String executeUser;

    /**
     * start time
     */
    private LocalDateTime executeTime;

    /**
     * end time
     */
    private LocalDateTime resultTime;

    /**
     * execute duration
     */
    private Integer executeDuration;

    /**
     * execute result
     */
    private String executeResult;

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

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    public LocalDateTime getResultTime() {
        return resultTime;
    }

    public void setResultTime(LocalDateTime resultTime) {
        this.resultTime = resultTime;
    }

    public Integer getExecuteDuration() {
        return executeDuration;
    }

    public void setExecuteDuration(Integer executeDuration) {
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
        return "YcFunctionExecuteRecord{" +
            "id = " + id +
            ", functionName = " + functionName +
            ", executeArguments = " + executeArguments +
            ", executeUser = " + executeUser +
            ", executeTime = " + executeTime +
            ", resultTime = " + resultTime +
            ", executeDuration = " + executeDuration +
            ", executeResult = " + executeResult +
        "}";
    }
}
