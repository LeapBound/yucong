package yzggy.yucong.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.chat.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.entities.FunctionEntity;
import yzggy.yucong.mapper.FunctionMapper;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.FuncService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncServiceImpl implements FuncService {

    private final FunctionMapper functionMapper;
    private final ConversationService conversationService;
    private final RestTemplate actionRestTemplate;

    @Override
    public List<Functions> getListByAccountIdAndBotId(String accountId, String botId) {
        // 获取账号function列表
        List<FunctionEntity> functionList = this.functionMapper.listByAccountId(accountId);
        if (functionList != null && functionList.size() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            List<Functions> functions = new ArrayList<>(functionList.size());
            functionList.forEach(entity -> {
                try {
                    MyFunctions myFunctions = mapper.readValue(entity.getFunctionJson(), MyFunctions.class);
                    Parameters parameters = Parameters.builder()
                            .type(myFunctions.getParameters().getType())
                            .properties(myFunctions.getParameters().getProperties())
                            .required(myFunctions.getParameters().getRequired())
                            .build();
                    functions.add(Functions.builder()
                            .name(myFunctions.getName())
                            .description(myFunctions.getDescription())
                            .parameters(parameters)
                            .build());
                } catch (JsonProcessingException e) {
                    log.error("getListByAccountIdAndBotId error", e);
                }
            });
            log.info("body {}", functions);
            return functions;
        }

        return null;
    }

    @Override
    public void invokeFunc(String botId, String accountId, FunctionCall functionCall) {
        try {
            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            // body
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(functionCall);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);
            ResponseEntity<Message> entity = this.actionRestTemplate.postForEntity("/yc/function/openai/execute", requestEntity, Message.class);

            Message message = entity.getBody();
            if (message != null) {
                log.info("body {}", message);
                this.conversationService.addMessage(botId, accountId, message);
            }
        } catch (Exception e) {
            log.error("invokeFunc error", e);
            Message message = new Message();
            message.setRole(Message.Role.SYSTEM.getName());
            message.setContent("关闭失败");
            this.conversationService.addMessage(botId, accountId, message);
        }
    }
}
