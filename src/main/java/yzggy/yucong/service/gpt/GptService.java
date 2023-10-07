package yzggy.yucong.service.gpt;

import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;

import java.math.BigDecimal;
import java.util.List;

public interface GptService {

    List<MyMessage> completions(String botId, String accountId, List<MyMessage> messageList);

    String summary(String content);

    List<BigDecimal> embedding(String content);
}
