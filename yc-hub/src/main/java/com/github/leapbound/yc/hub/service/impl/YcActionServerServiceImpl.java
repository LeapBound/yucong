package com.github.leapbound.yc.hub.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leapbound.sdk.llm.chat.func.MyFunctionCall;
import com.github.leapbound.yc.hub.mapper.AccountMapper;
import com.github.leapbound.yc.hub.model.FunctionExecResultDto;
import com.github.leapbound.yc.hub.model.process.ProcessResponseDto;
import com.github.leapbound.yc.hub.service.YcActionServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Fred
 * @date 2024/4/8 11:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YcActionServerServiceImpl implements YcActionServerService {

    private final RestTemplate actionRestTemplate;
    private final ObjectMapper objectMapper;
    private final AccountMapper accountMapper;

    @Override
    public FunctionExecResultDto invokeFunc(String botId, String accountId, MyFunctionCall functionCall) {
        try {
//            LambdaQueryWrapper<AccountEntity> lqw = new LambdaQueryWrapper<AccountEntity>()
//                    .eq(AccountEntity::getAccountId, accountId);
//            AccountEntity accountEntity = this.accountMapper.selectOne(lqw);

            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("accountId", accountId);
            requestHeaders.add("botId", botId);
//            requestHeaders.add("externalId", accountEntity.getExternalId());
            // body
            String json = this.objectMapper.writeValueAsString(functionCall);
            log.debug("invokeFunc json {}", json);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);
            ResponseEntity<ProcessResponseDto> entity = this.actionRestTemplate.postForEntity(
                    "/function/openai/execute",
                    requestEntity,
                    ProcessResponseDto.class);

            ProcessResponseDto responseDto = entity.getBody();
            log.debug("invokeFunc {}", responseDto);
            FunctionExecResultDto execResultDto = new FunctionExecResultDto();
            execResultDto.setExecuteResult(responseDto.getSuccess());
            if (responseDto.getData() != null && responseDto.getData() instanceof String) {
                execResultDto.setMsg((String) responseDto.getData());
            }
            return execResultDto;
        } catch (Exception e) {
            log.error("invokeFunc error", e);
        }

        return new FunctionExecResultDto(false, null);
    }

}
