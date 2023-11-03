package yzggy.yucong.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.service.gpt.FuncService;
import yzggy.yucong.service.gpt.GptHandler;
import yzggy.yucong.service.gpt.GptService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final FuncService funcService;
    private final GptHandler openAiHandler;
    private final GptHandler qianfanHandler;

    @Override
    public List<MyMessage> completions(String botId, String accountId, List<MyMessage> messageList) {
        MyChatCompletionResponse response = sendToChatServer(botId, accountId, messageList);
        List<MyMessage> gptMessageList = new ArrayList<>(2);

        // 处理方法调用
        if (response.getMessage().getFunctionCall() != null) {
            MyMessage message = this.funcService.invokeFunc(botId, accountId, response.getMessage().getFunctionCall());
            messageList.add(message);
            gptMessageList.add(message);

            response = sendToChatServer(botId, accountId, messageList);
        }

        // 助理消息
        MyMessage assistantMsg = new MyMessage();
        assistantMsg.setRole(response.getMessage().getRole());
        assistantMsg.setContent(response.getMessage().getContent());
        messageList.add(assistantMsg);
        gptMessageList.add(assistantMsg);

        return gptMessageList;
    }

    @Override
    public String summary(String content) {
        return this.openAiHandler.summary(content).getMessage().getContent();
    }

    @Override
    public List<BigDecimal> embedding(String content) {
        return this.openAiHandler.embedding(content);
    }

    private MyChatCompletionResponse sendToChatServer(String botId, String accountId, List<MyMessage> messageList) {
        List<MyFunctions> functionsList = this.funcService.getListByAccountIdAndBotId(accountId, botId);
        return this.openAiHandler.chatCompletion(messageList, functionsList);
//        return this.qianfanHandler.chatCompletion(messageList, functionsList);
    }
}
