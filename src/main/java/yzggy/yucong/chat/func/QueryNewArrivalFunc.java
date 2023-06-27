package yzggy.yucong.chat.func;

import cn.hutool.json.JSONObject;
import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Parameters;

import java.util.ArrayList;

public class QueryNewArrivalFunc implements BaseFunc {

    @Override
    public Functions get() {
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
                .name("queryNewArrival")
                .description("获取上新的时间")
                .parameters(parameters)
                .build();
    }
}
