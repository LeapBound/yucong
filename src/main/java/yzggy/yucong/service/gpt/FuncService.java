package yzggy.yucong.service.gpt;

import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctionCall;
import yzggy.yucong.chat.func.MyFunctions;

import java.util.List;

public interface FuncService {

    List<MyFunctions> getListByAccountIdAndBotId(String accountName, String botId);

    MyMessage invokeFunc(String botId, String accountId, MyFunctionCall functionCall);
}
