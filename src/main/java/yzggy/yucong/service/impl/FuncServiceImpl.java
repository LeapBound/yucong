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
import yzggy.yucong.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncServiceImpl implements FuncService {

    private final ConversationService conversationService;
    private final UserService userService;
    private final QueryNewArrivalFunc queryNewArrivalFunc;

    @Override
    public List<Functions> getListByUserIdAndBotId(String userId, String botId) {
        // 获取商户的所有功能

        // 判断用户function权限
        List<String> autList = this.userService.getAuthByUserId(userId);

        List<Functions> funcList = new ArrayList<>();
        funcList.add(new QueryNewArrivalFunc().getDefinition());
        return funcList;
    }

    @Override
    public void invokeFunc(String userId, FunctionCall functionCall) {
        if (this.queryNewArrivalFunc.getName().equals(functionCall.getName())) {
            List<Message> messageList = this.queryNewArrivalFunc.execute(functionCall);
            this.conversationService.addMessages(userId, messageList);
        }
    }
}
