package yzggy.yucong.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctionCall;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.entities.FunctionEntity;
import yzggy.yucong.mapper.FunctionMapper;
import yzggy.yucong.model.process.ProcessTaskDto;
import yzggy.yucong.service.gpt.FuncService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncServiceImpl implements FuncService {

    private final FunctionMapper functionMapper;
    private final RestTemplate actionRestTemplate;

    @Override
    public List<MyFunctions> getListByAccountIdAndBotId(String accountId, String botId, ProcessTaskDto task) {
        // 获取账号function列表
        List<FunctionEntity> functionList = null;
        if (task == null) {
            functionList = this.functionMapper.listByAccountId(accountId);
        } else if (StringUtils.hasText(task.getTaskName())) {
            functionList = this.functionMapper.listByTaskName(task.getTaskName());
        }

        if (functionList != null && !functionList.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            List<MyFunctions> functions = new ArrayList<>(functionList.size());
            functionList.forEach(entity -> {
                try {
                    MyFunctions myFunctions = mapper.readValue(entity.getFunctionJson(), MyFunctions.class);
                    log.info("待执行的function: {}", myFunctions.getName());
                    functions.add(myFunctions);
                } catch (JsonProcessingException e) {
                    log.error("getListByAccountIdAndBotId error", e);
                }
            });
            return functions;
        }

        return null;
    }

    @Override
    public MyMessage invokeFunc(String botId, String accountId, MyFunctionCall functionCall) {
        try {
            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("accountId", accountId);
            requestHeaders.add("deviceId", "deviceId001");
            // body
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(functionCall);
            log.debug("invokeFunc json: {}", json);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);
            ResponseEntity<MyMessage> entity = this.actionRestTemplate.postForEntity("/yc/function/openai/execute", requestEntity, MyMessage.class);

            MyMessage message = entity.getBody();
            if (message != null) {
                log.info("执行方法返回: {}", message);
                return message;
            }
        } catch (Exception e) {
            log.error("invokeFunc error", e);
        }

        MyMessage message = new MyMessage();
        message.setRole(Message.Role.SYSTEM.getName());
        message.setContent("处理失败");
        return message;
    }
}
