package yzggy.yucong;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@SpringBootTest
@ActiveProfiles(value = "api2d")
class YucongApplicationTests {

    @Value("${api2d.api.base}")
    private String base;
    @Value("${api2d.api.key}")
    private String key;

    @Test
    void contextLoads() {
    }

    @Test
    void openAi() {
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(Collections.singletonList(this.key))
                // 自定义key的获取策略：默认KeyRandomStrategy
                // .keyStrategy(new KeyRandomStrategy())
                .keyStrategy(new FirstKeyStrategy())
                // 自己做了代理就传代理地址，没有可不不传
                .apiHost(this.base)
                .build();

        // 聊天模型：gpt-3.5
        Message message = Message.builder().role(Message.Role.USER).content("你好啊我的伙伴！").build();
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message)).build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
        chatCompletionResponse.getChoices().forEach(e -> {
            log.info("{}", e.getMessage());
        });

    }
}
