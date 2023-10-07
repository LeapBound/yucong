package yzggy.yucong.service.gpt;

import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctions;

import java.math.BigDecimal;
import java.util.List;

public interface GptHandler {

    MyChatCompletionResponse chatCompletion(List<MyMessage> messageList, List<MyFunctions> functionsList);

    MyChatCompletionResponse summary(String content);

    List<BigDecimal> embedding(String content);
}
