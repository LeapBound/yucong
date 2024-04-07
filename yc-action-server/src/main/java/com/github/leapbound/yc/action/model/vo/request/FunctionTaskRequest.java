package com.github.leapbound.yc.action.model.vo.request;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * function <-> task service
 *
 * @author tangxu
 * @since 2024/3/29 17:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FunctionTaskRequest extends BaseRequest {

    private Integer id;

    private String processId;

    /**
     * 方法名
     */
    private String functionName;

    /**
     *  task name
     */
    private String taskName;

    /**
     * 话术
     */
    private List<Script> script;

    public String scriptJson() {
        return JSON.toJSONString(this.script);
    }

    /**
     * 话术规则
     */
    @Data
    public static class Script {

        /**
         * 类型：
         * 执行成功/执行失败（外部接口）/调用超时/异常失败（本地异常）
         * SUCCESS/FAIL/TIMEOUT/EXCEPTION
         */
        private String type;

        /**
         * 对应的提示信息
         */
        private String message;


    }

}
