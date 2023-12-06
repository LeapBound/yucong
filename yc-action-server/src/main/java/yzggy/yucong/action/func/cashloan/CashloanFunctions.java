package yzggy.yucong.action.func.cashloan;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import yzggy.yucong.action.webClient.GutsHubService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author yamath
 * @since 2023/10/10 14:35
 */
@Component
public class CashloanFunctions {

    private static final Logger logger = LoggerFactory.getLogger(CashloanFunctions.class);

    private final GutsHubService gutsHubService;

    public CashloanFunctions(GutsHubService gutsHubService) {
        this.gutsHubService = gutsHubService;
    }

    public JSONObject applyLoan(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            result = checkApplyLoanArguments(arguments);
            if (!result.isEmpty()) {
                return result;
            }
            //
            String name = arguments.getString("name");
            String idNo = arguments.getString("idNo");
            String mobile = arguments.getString("mobile");
            String accountId = arguments.getString("accountId");
            // need confirm
            if (!arguments.containsKey("confirm")) {
                result.put("消息", "请仔细确认提供的信息 姓名: " + name + " , 身份证: " + idNo + " , 手机号: " + mobile);
                return result;
            }
            //
            JSONObject jsonObject = gutsHubService.applySubmit(name, idNo, mobile, accountId);
            if (jsonObject != null) {
                String message = jsonObject.getString("data");
                result.put("结果", message);
            } else {
                result.put("结果", "申请贷款失败");
            }
            return result;
        } catch (Exception ex) {
            logger.error("cash loan applyLoan error,", ex);
            result.put("错误", "申请贷款时发生错误，联系管理员");
            return result;
        }
    }

    public JSONObject applyAuditStatus(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            result = checkApplyAuditStatusArguments(arguments);
            if (!result.isEmpty()) {
                return result;
            }
            String orderType = arguments.getString("orderType");
            String accountId = arguments.getString("accountId");
            //
            JSONObject jsonObject = null;
            if ("预审".equals(orderType)) {
                jsonObject = gutsHubService.applyAuditStatus(accountId);
            } else if ("提现".equals(orderType)) {
                jsonObject = new JSONObject();
                jsonObject.put("data", "正在放款中");
            }
            if (jsonObject != null) {
                String message = jsonObject.getString("data");
                result.put("结果", message);
            } else {
                result.put("结果", "查询" + orderType + "贷款进度失败");
            }
            return result;
        } catch (Exception ex) {
            logger.error("cash loan applyAuditStatus error,", ex);
            result.put("错误", "查询贷款进度时发生错误，联系管理员");
            return result;
        }
    }

    public JSONObject bindCard(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            result = checkBindCardArguments(arguments);
            if (!result.isEmpty()) {
                return result;
            }
            //
            String cardNo = arguments.getString("cardNo");
            String cardMobile = arguments.getString("cardMobile");
            String accountId = arguments.getString("accountId");
            if (!arguments.containsKey("confirm")) {
                result.put("消息", "请仔细确认提供的信息 银行卡号: " + cardNo + " , 银行卡预留手机号: " + cardMobile);
                return result;
            }
            JSONObject jsonObject = gutsHubService.bindBankCard(accountId, cardNo, cardMobile);
            if (jsonObject != null) {
                String message = jsonObject.getString("data");
                result.put("结果", message);
            } else {
                result.put("结果", "绑定提现用的银行卡失败");
            }
            return result;
        } catch (Exception ex) {
            logger.error("cash loan bindCard error,", ex);
            result.put("错误", "绑定提现用的银行卡时发生错误，请联系管理员");
            return result;
        }
    }

    public JSONObject bindCardCaptcha(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            result = checkBindCardCaptchaArguments(arguments);
            if (!result.isEmpty()) {
                return result;
            }
            //
            String verifyCode = arguments.getString("verifyCode");
            String accountId = arguments.getString("accountId");
            JSONObject jsonObject = gutsHubService.bindBankCardCaptcha(accountId, verifyCode);
            if (jsonObject != null) {
                String message = jsonObject.getString("data");
                result.put("结果", message);
            } else {
                result.put("结果", "验证银行卡验证码失败");
            }
            return result;
        } catch (Exception ex) {
            logger.error("cash loan bindCardCaptcha error,", ex);
            result.put("错误", "验证银行卡验证码时发生错误，请联系管理员");
            return result;
        }
    }

    public JSONObject loanSubmit(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            result = checkLoanSubmitArguments(arguments);
            if (!result.isEmpty()) {
                return result;
            }
            //
            BigDecimal loanAmount = new BigDecimal(arguments.get("loanAmount").toString());
            int amount = loanAmount.setScale(0, RoundingMode.UP).intValue();
            int period = arguments.getInteger("period");
            if (!arguments.containsKey("confirm")) {
                result.put("消息", "请仔细确认 提现金额: " + amount + " ,期数: " + period);
                return result;
            }
            String accountId = arguments.getString("accountId");
            JSONObject jsonObject = gutsHubService.loanSubmit(accountId, amount, period);
            if (jsonObject != null) {
                String message = jsonObject.getString("data");
                result.put("结果", message);
            } else {
                result.put("结果", "放款失败");
            }
            return result;
        } catch (Exception ex) {
            logger.error("cash loan loanSubmit error,", ex);
            result.put("错误", "放款时发生错误，请联系管理员");
            return result;
        }
    }

    private static JSONObject checkApplyLoanArguments(JSONObject arguments) {
        JSONObject result = new JSONObject();
        if (!arguments.containsKey("name") || !arguments.containsKey("idNo") || !arguments.containsKey("mobile")) {
            result.put("错误", "请提供 姓名，身份证，手机号，并仔细确认");
            return result;
        }
        String name = arguments.getString("name");
        String idNo = arguments.getString("idNo");
        String mobile = arguments.getString("mobile");
        if (StrUtil.isEmptyIfStr(name)) {
            result.put("错误", "请提供姓名");
            return result;
        }
        if (StrUtil.isEmptyIfStr(idNo)) {
            result.put("错误", "请提供身份证");
            return result;
        }
        if (StrUtil.isEmptyIfStr(mobile)) {
            result.put("错误", "请提供手机号");
            return result;
        }
        return result;
    }

    private static JSONObject checkApplyAuditStatusArguments(JSONObject arguments) {
        JSONObject result = new JSONObject();
        //
        if (!arguments.containsKey("orderType")) {
            result.put("结果", "需要查询哪种订单类型，预审提现");
            return result;
        }
        //
        String orderType = arguments.getString("orderType");
        if (StrUtil.isEmptyIfStr(orderType)) {
            result.put("错误", "需要提供查询的订单类型");
        }
        return result;
    }

    private static JSONObject checkBindCardArguments(JSONObject arguments) {
        JSONObject result = new JSONObject();
        if (!arguments.containsKey("cardNo") || !arguments.containsKey("cardMobile")) {
            result.put("错误", "请提供 银行卡号，银行卡预留手机号，并仔细确认");
            return result;
        }
        String cardNo = arguments.getString("cardNo");
        String cardMobile = arguments.getString("cardMobile");
        if (StrUtil.isEmptyIfStr(cardNo)) {
            result.put("错误", "请提供银行卡号");
            return result;
        }
        if (StrUtil.isEmptyIfStr(cardMobile)) {
            result.put("错误", "请提供银行卡预留手机号");
            return result;
        }
        return result;
    }

    private static JSONObject checkBindCardCaptchaArguments(JSONObject arguments) {
        JSONObject result = new JSONObject();
        if (!arguments.containsKey("verifyCode")) {
            result.put("错误", "请提供验证码");
            return result;
        }
        String verifyCode = arguments.getString("verifyCode");
        if (StrUtil.isEmptyIfStr(verifyCode)) {
            result.put("错误", "请提供验证码");
            return result;
        }
        return result;
    }

    private static JSONObject checkLoanSubmitArguments(JSONObject arguments) {
        JSONObject result = new JSONObject();
        if (!arguments.containsKey("loanAmount") || !arguments.containsKey("period")) {
            result.put("错误", "请提供 提现金额和期数");
            return result;
        }
        if (StrUtil.isEmptyIfStr(arguments.get("loanAmount").toString())) {
            result.put("错误", "请提供提现金额");
            return result;
        }
        Integer period = arguments.getInteger("period");
        if (period == null) {
            result.put("错误", "请提供期数");
            return result;
        }
        return result;
    }
}
