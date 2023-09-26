package yzggy.yucong.service.impl.gpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.bce.QianfanApiClient;
import yzggy.yucong.chat.dialog.MyChatCompletionResponse;
import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.service.GptHandler;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QianfanHandler implements GptHandler {

    private final QianfanApiClient qianfanApiClient;

    @Override
    public MyChatCompletionResponse chatCompletion(List<MyMessage> messageList, List<MyFunctions> functionsList) {
        return null;
    }
}
