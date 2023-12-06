package yzggy.yucong.action.func.weather;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * @author yamath
 * @since 2023/7/12 10:28
 */
@Component
public class WeatherFunctions {

    private static final String KEY_LOCATION = "location";
    private static final String KEY_FORMAT = "format";

    public JSONObject getDayWeatherForecast(JSONObject argument) {
        String location = "";
        String format = "摄氏度";

        if (argument.containsKey(KEY_LOCATION)) {
            location = argument.getString(KEY_LOCATION);
        }
        if (argument.containsKey(KEY_FORMAT)) {
            format = argument.getString(KEY_FORMAT);
        }
        JSONObject result = new JSONObject();
        if (StrUtil.isEmptyIfStr(location)) {
            result.put("错误", "没有提供 location，要求用户明确 location.");
            return result;
        }
        result.put(KEY_LOCATION, location);
        result.put("天气", "晴朗");
        result.put("温度", "32");
        result.put("空气质量指数", "30");
        result.put("紫外线指数", "5");
        result.put("风速", "5m/s");
        return result;
    }

    public JSONObject getCurrentWeather(JSONObject argument) {
        String location = "";
        String format = "摄氏度";
        Integer days = 1;
        if (argument.containsKey(KEY_LOCATION)) {
            location = argument.getString(KEY_LOCATION);
        }
        if (argument.containsKey(KEY_FORMAT)) {
            format = argument.getString(KEY_FORMAT);
        }
        if (argument.containsKey("num_days")) {
            days = argument.getInteger("num_days");
        }
        JSONObject result = new JSONObject();
        if (StrUtil.isEmptyIfStr(location)) {
            result.put("错误", "没有提供 location，要求用户明确 location.");
            return result;
        }
        result.put(KEY_LOCATION, location);
        result.put("第一天", "天气: 晴朗, 温度: 23-35, 空气质量指数: 60");
        result.put("第二天", "天气: 小雨转大雨, 温度: 20-28");
        result.put("第三天", "天气: 雷暴, 温度: 10-16");
        return result;
    }
}
