package com.github.leapbound;

import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Fred Gu
 * @date 2024-12-06 8:52
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class SdkTests {

    @Autowired
    private ArkService arkService;
    @Value("${yucong.llm.doubao.endpointId}")
    private String endpointId;

    @Test
    void doubaoTest() {
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("问题没解决").build();
        messages.add(userMessage);

        final List<ChatTool> tools = List.of(
                new ChatTool(
                        "function",
                        new ChatFunction.Builder()
                                .name("check_problem_solved")
                                .description("判断用户的问题是否解决了")
                                .parameters(new FunctionParamModel(
                                        "object",
                                        new HashMap<>() {{
                                            put("problemSolved", new HashMap<String, Object>() {{
                                                put("type", "boolean");
                                                put("enum", List.of(true, false));
                                                put("description", "问题是否已经解决了");
                                            }});
                                        }},
                                        Collections.singletonList("problemSolved")
                                ))
                                .build()
                )
        );

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(this.endpointId)
                .messages(messages)
                .tools(tools)
                .build();
        log.info("sendToChatServer chatCompletionRequest: {}", chatCompletionRequest);

        ChatCompletionChoice chatCompletionChoice = this.arkService.createChatCompletion(chatCompletionRequest).getChoices().get(0);
        log.info("sendToChatServer chatCompletionChoice: {}", chatCompletionChoice);
    }
}
