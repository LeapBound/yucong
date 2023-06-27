package yzggy.yucong.service.impl;

import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.chat.func.QueryNewArrivalFunc;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.FuncService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncServiceImpl implements FuncService {

    private final ConversationService conversationService;
    private final QueryNewArrivalFunc queryNewArrivalFunc;

    @Override
    public List<Functions> getListByBotId(String botId) {
        List<Functions> funcList = new ArrayList<>();
        funcList.add(new QueryNewArrivalFunc().getDefinition());
        return funcList;
    }

    @Override
    public void invokeFunc(String userId, FunctionCall functionCall) {
        if (this.queryNewArrivalFunc.getName().equals(functionCall.getName())) {
            List<Message> messageList = this.queryNewArrivalFunc.execute(functionCall);
            this.conversationService.addMessages(userId,  messageList);
        }
    }

}
