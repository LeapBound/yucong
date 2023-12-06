package yzggy.yucong.action.func.sales;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/7/14 11:35
 */
@Component
public class InstallmentFunctions {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_IDCARD_NUM = "idcardNum";
    private static final String KEY_MOBILE_NUM = "mobileNum";
    private static final String KEY_AMOUNT = "amount";

    private static Map<String, String> planMap = Maps.newHashMap();

    static {
        planMap.put("20230810", "2500");
        planMap.put("20230910", "2500");
        planMap.put("20231010", "2500");
        planMap.put("20231110", "2500");
        planMap.put("20231210", "2500");
        planMap.put("20240110", "2500");
        planMap.put("20240210", "2500");
        planMap.put("20240310", "2500");
        planMap.put("20240410", "2500");
        planMap.put("20240510", "2500");
        planMap.put("20240610", "2500");
        planMap.put("20240710", "2500");
    }

    public JSONObject commitFirstTrailAndUserInfo(JSONObject arguments) {
        JSONObject result = checkArguments(arguments);
        if (result != null) {
            return result;
        }
        // do 分期 初审
        result.put(KEY_USERNAME, arguments.get(KEY_USERNAME));
        result.put("结果", "恭喜，初审通过");
        result.put("12期还款计划", planMap);
        result.put("提示", "以上是还款计划，如果确认没问题的话请点击链接做人脸识别进行身份证核验。");
        result.put("链接", "https://beta.geexfinance.com/robo/yucong/api/ocr");
        return result;
    }

    public JSONObject getRepayPlan(JSONObject arguments) {
        JSONObject result = checkArguments(arguments);
        if (result != null) {
            return result;
        }
        return result;
    }

    private static JSONObject checkArguments(JSONObject arguments) {
        JSONObject result = new JSONObject();
        if (arguments == null) {
            result.put("错误", "没有用户的信息，要求用户提供必要的信息");
            return result;
        }

        if (!arguments.containsKey(KEY_USERNAME)) {
            result.put("错误", "没有提供用户的姓名，要求用户提供身份证真实姓名");
            return result;
        }
        if (!arguments.containsKey(KEY_IDCARD_NUM)) {
            result.put("错误", "用户没有提供身份证号，要求用户提供身份证号");
            return result;
        }
        if (!arguments.containsKey(KEY_MOBILE_NUM)) {
            result.put("错误", "用户没有提供手机号，要求用户提供真实手机号");
            return result;
        }
        if (!arguments.containsKey(KEY_AMOUNT)) {
            result.put("错误", "用户没有提供分期金额，要求用户提供分期的金额");
            return result;
        }
        return null;
    }
}
