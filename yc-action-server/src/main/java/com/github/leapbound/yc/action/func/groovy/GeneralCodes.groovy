package com.github.leapbound.yc.action.func.groovy
/**
 *
 * @author yamath
 * @since 2024/7/5 10:11
 */

// 100 ~ 199 正在处理中状态，还有后续处理
// 200 ~ 299 成功状态，已正确处理
// 300 ~ 399 重定向，需要重新发送请求
// 400 ~ 499 客户端错误，请求有误，服务端无法处理
// 500 ~ 599 服务端错误，服务端处理时内部发生错误
// 1000 ~ 1999 缺少内部方法
// 2000 ~ 2999 缺少必要参数
// 3000 ~ 3999 逻辑处理失败，返回错误信息
class GeneralCodes {
    static final int MISSING_EXEC_METHOD = 1001 // 没有执行方法
    static final int MISSING_REQUEST_PARAMS = 2001 // 缺少必要参数
    static final int PROCESS_FAILED = 3000 // 流程失败
    static final int PROCESS_FAILED_PROCESS_START_FAILED = 3001; // 流程启动失败
    static final int PROCESS_FAILED_PROCESS_MISSING = 3002; // 没有流程
    static final int PROCESS_FAILED_TASK_NOT_MATCH = 3003; // 当前任务与方法不匹配
    static final int PROCESS_FAILED_VARIABLES_INVALID = 3004 // 流程参数无效
    static final int PROCESS_FAILED_COMPLETE_FAILED = 3005 // 流程完成失败
    static final int LOGIC_FAILED = 3100 // 逻辑失败
    static final int LOGIC_FAILED_PARAMS_INVALID = 3101 // 参数无效
    static final int LOGIC_FAILED_RESULT_INVALID = 3102 // 结果不正确
    static final int REST_CALL_FAILED = 3200 // rest call 失败
    static final int REST_CALL_FAILED_NO_RESPONSE = 3201 // rest call 无反应
    static final int REST_CALL_FAILED_TIME_OUT = 3202 // rest call 超时
    static final int REST_CALL_FAILED_CLIENT_FAILED = 3203 // rest call 客户端错误
    static final int REST_CALL_FAILED_SERVER_FAILED = 3204 // rest call 服务端错误
    static final int LOGIC_EXCEPTION = 3900 // 逻辑异常
    static final int UNKNOWN_EXCEPTION = 9000 // 未知异常
}




