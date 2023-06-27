package yzggy.yucong.chat.func;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.chat.Parameters;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueryNewArrivalFunc extends BaseFunc {

    public QueryNewArrivalFunc() {
        this.name = "queryNewArrival";
    }

    @Override
    public Functions getDefinition() {
        // 属性一
        JSONObject brand = new JSONObject();
        brand.putOpt("brand", "string");
        brand.putOpt("description", "品牌");

        // 参数
        JSONObject properties = new JSONObject();
        properties.putOpt("brand", brand);
        Parameters parameters = Parameters.builder()
                .type("object")
                .properties(properties)
                .required(new ArrayList<>(0))
                .build();

        return Functions.builder()
                .name(getName())
                .description("获取上新的时间")
                .parameters(parameters)
                .build();
    }

    @Override
    public List<Message> execute(FunctionCall functionCall) {
        QueryNewArrivalParam param = JSONUtil.toBean(functionCall.getArguments(), QueryNewArrivalParam.class);
        String newArrivalTime = "2023-07-01";

        Message message1 = Message.builder().role(Message.Role.ASSISTANT).content("方法参数").functionCall(functionCall).build();
        String content = "{" +
                "\"newArrivalTime\": \"" + newArrivalTime + "\"" +
                "}";
        Message message2 = Message.builder().role(Message.Role.FUNCTION).name("queryNewArrival").content(content).build();

        return List.of(message1, message2);
    }
}
