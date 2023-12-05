package yzggy.yucong.service.gpt;

import yzggy.yucong.chat.dialog.MyMessage;
import yzggy.yucong.chat.func.MyFunctionCall;
import yzggy.yucong.chat.func.MyFunctions;
import yzggy.yucong.model.process.ProcessTaskDto;

import java.util.List;

public interface FuncService {

    List<MyFunctions> getListByAccountIdAndBotId(String accountName, String botId, ProcessTaskDto task);

    MyMessage invokeFunc(String botId, String accountId, MyFunctionCall functionCall);
}
