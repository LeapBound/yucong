package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Functions;
import yzggy.yucong.chat.func.MyFunctionCall;
import yzggy.yucong.chat.func.MyFunctions;

import java.util.List;

public interface FuncService {

    List<MyFunctions> getListByAccountIdAndBotId(String accountName, String botId);

    void invokeFunc(String botId, String userId, MyFunctionCall functionCall);
}
