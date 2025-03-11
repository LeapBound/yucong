package com.github.leapbound.yc.hub.service.impl.function.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fred Gu
 * @date 2024-12-05 13:11
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessFunction {

    private final ProcessService processService;

    public FunctionExecResultDto exec(String botId, String accountId, MyFunctionCall functionCall) {
        Map<String, Object> startFormVariables = new HashMap<>();
        startFormVariables.put("accountId", accountId);
        startFormVariables.put("botId", botId);

        String processKey = null;
        switch (functionCall.getName()) {
            case "start_ticket":
                processKey = "Process_bd_qa";
                JSONObject args = JSON.parseObject(functionCall.getArguments());
                String question = args.getString("question");
                switch (question) {
                    case "验证码问题":
                        startFormVariables.put("question", "question_verification_code");
                        break;
                    case "用户需要还款的金额":
                        startFormVariables.put("question", "question_repay_amount");
                        break;
                }
        }

        if (StringUtils.hasText(processKey)) {
            String processInstanceId = this.processService.startProcess(processKey, accountId, startFormVariables);
            log.info("{}, start process", accountId);
            return new FunctionExecResultDto(true, processInstanceId);
        }
        return new FunctionExecResultDto(false, null);
    }
}