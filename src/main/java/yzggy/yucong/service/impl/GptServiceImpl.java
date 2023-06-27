package yzggy.yucong.service.impl;

import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.func.QueryNewArrivalFunc;
import yzggy.yucong.chat.func.QueryNewArrivalParam;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.GptService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final OpenAiClient openAiClient;

    @Override
    public void chat(SingleChatModel singleChatModel) {
        // 消息
        Message systemMsg = Message.builder()
                .role(Message.Role.SYSTEM)
                .content("你现在是一个客服人员")
                .build();
        Message userMsg = Message.builder()
                .role(Message.Role.USER)
                .content(singleChatModel.getContent())
                .build();

        // func构造
        Functions queryNewArrivalFunctions = new QueryNewArrivalFunc().get();

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(Arrays.asList(systemMsg, userMsg))
                .functions(Collections.singletonList(queryNewArrivalFunctions))
                .functionCall("auto")
                .model(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName())
                .build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
        ChatChoice chatChoice = chatCompletionResponse.getChoices().get(0);

        if (chatChoice.getMessage().getFunctionCall() != null) {
            FunctionCall functionCall = chatChoice.getMessage().getFunctionCall();
            log.info("构造的方法值：{}", functionCall);
            log.info("构造的方法名称：{}", functionCall.getName());
            log.info("构造的方法参数：{}", functionCall.getArguments());

            switch (functionCall.getName()) {
                case "queryNewArrival":
                    QueryNewArrivalParam wordParam = JSONUtil.toBean(chatChoice.getMessage().getFunctionCall().getArguments(), QueryNewArrivalParam.class);
                    String newArrivalTime = getNewArrival(wordParam);

                    Message message2 = Message.builder().role(Message.Role.ASSISTANT).content("方法参数").functionCall(functionCall).build();
                    String content
                            = "{ " +
                            "\"newArrivalTime\": \"" + newArrivalTime + "\"" +
                            "}";
                    Message message3 = Message.builder().role(Message.Role.FUNCTION).name("queryNewArrival").content(content).build();
                    List<Message> messageList = Arrays.asList(systemMsg, userMsg, message2, message3);
                    ChatCompletion chatCompletionV2 = ChatCompletion
                            .builder()
                            .messages(messageList)
                            .model(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName())
                            .build();
                    ChatCompletionResponse chatCompletionResponseV2 = openAiClient.chatCompletion(chatCompletionV2);
                    log.info("自定义的方法返回值：{}", chatCompletionResponseV2.getChoices().get(0).getMessage().getContent());
                    break;
                default:
            }
        }
    }

    private String getNewArrival(QueryNewArrivalParam wordParam) {
        return "2023-07-01";
    }
}
