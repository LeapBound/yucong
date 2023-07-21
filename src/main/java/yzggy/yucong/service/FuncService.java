package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Functions;

import java.util.List;

public interface FuncService {

    List<Functions> getListByAccountIdAndBotId(String accountName, String botId);

    void invokeFunc(String userId, FunctionCall functionCall);
}
