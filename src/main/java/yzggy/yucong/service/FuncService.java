package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Functions;

import java.util.List;

public interface FuncService {

    List<Functions> getListByBotId(String botId);

    void invokeFunc(String userId, FunctionCall functionCall);
}
