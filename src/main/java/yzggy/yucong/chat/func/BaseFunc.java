package yzggy.yucong.chat.func;

import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Message;

import java.util.List;

public abstract class BaseFunc {

    protected String name;
    protected String auth;

    public String getName() {
        return this.name;
    }

    public String getAuth() {
        return this.auth;
    }

    public abstract Functions getDefinition();

    public abstract List<Message> execute(FunctionCall functionCall);
}
