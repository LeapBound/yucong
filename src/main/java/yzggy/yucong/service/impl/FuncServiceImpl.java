package yzggy.yucong.service.impl;

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
import yzggy.yucong.entities.RoleEntity;
import yzggy.yucong.mapper.RoleMapper;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.FuncService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncServiceImpl implements FuncService {

    private final RoleMapper roleMapper;
    private final ConversationService conversationService;
    private final RestTemplate actionRestTemplate;

    @Override
    public List<Functions> getListByUserIdAndBotId(String userId, String botId) {
        // 获取bot的的角色
        List<RoleEntity> roleList = this.roleMapper.selectRoleByBotId(botId);

        // 判断用户function权限
        // TODO: 2023/7/13
        List<String> autList = new ArrayList<>(roleList.size());
        roleList.forEach(role -> autList.add(role.getRoleName()));

        try {
            // 从action server获取方法列表
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            // body
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(autList);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);
            ResponseEntity<MyFunctions[]> entity = this.actionRestTemplate.postForEntity("/yc-action-server/yc/function/openai/list", requestEntity, MyFunctions[].class);

            if (entity.getBody() != null) {
                List<Functions> functions = new ArrayList<>(entity.getBody().length);
                for (MyFunctions myFunctions : entity.getBody()) {
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
                }
                log.info("body {}", functions);
                return functions;
            }
        } catch (Exception e) {
            log.error("getListByUserIdAndBotId error", e);
        }

        return null;
    }

    @Override
    public void invokeFunc(String userId, FunctionCall functionCall) {
        try {
            // 请求action server执行方法
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            // body
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(functionCall);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);
            ResponseEntity<Message> entity = this.actionRestTemplate.postForEntity("/yc-action-server/yc/function/openai/execute", requestEntity, Message.class);

            Message message = entity.getBody();
            if (message != null) {
                log.info("body {}", message);
                this.conversationService.addMessages(userId, List.of(message));
            }
        } catch (Exception e) {
            log.error("invokeFunc error", e);
        }
    }
}
