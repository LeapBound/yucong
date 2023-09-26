package yzggy.yucong.service;

import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctions;

import java.util.List;

public interface GptHandler {

    MyChatCompletionResponse chatCompletion(List<MyMessage> messageList, List<MyFunctions> functionsList);
}
