package yzggy.yucong.action.func.order;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import yzggy.yucong.action.func.order.dto.LoanInfoVo;
import yzggy.yucong.action.func.order.dto.LoanStatusVo;
import yzggy.yucong.action.func.order.dto.RepayPlanVo;
import yzggy.yucong.action.func.order.dto.TryOrderVo;
import yzggy.yucong.action.func.order.enums.LoanCertificateStatusEnum;
import yzggy.yucong.action.func.order.enums.LoanOrderStatusEnum;
import yzggy.yucong.action.webClient.GutsHubService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author yamath
 * @since 2023/8/9 11:46
 */
@Component
public class OrderFunctions {

    private static final Logger logger = LoggerFactory.getLogger(OrderFunctions.class);

    private static final String KEY_ORDER_NO = "orderNo";
    private static final String KEY_DATA = "data";
    private static final String ACTION_REPAY = "repay";
    private static final String ACTION_REFUND = "refund";

    private final GutsHubService gutsHubService;

    public OrderFunctions(GutsHubService gutsHubService) {
        this.gutsHubService = gutsHubService;
    }

    /**
     * 用户需要还多少钱
     *
     * @param arguments
     * @return
     */
    public JSONObject getUserRepaymentByOrder(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            String orderNo = checkOrderNo(arguments);
            if (StrUtil.isEmptyIfStr(orderNo)) {
                result.put("结果", "订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号");
                return result;
            }
            JSONObject jsonObject = this.gutsHubService.getRepayPlanByOrder(orderNo);
            if (jsonObject != null) {
                JSONArray array = jsonObject.getJSONArray(KEY_DATA);
                if (array != null && !array.isEmpty()) {
                    List<RepayPlanVo> list = array.toJavaList(RepayPlanVo.class);
                    int tenor = 0;
                    BigDecimal remain = new BigDecimal("0.0");
                    String date = "";
                    //
                    for (RepayPlanVo vo : list) {
                        //
                        if (!"正常".equals(vo.getStatus())) {
                            continue;
                        }
                        //
                        if (BigDecimal.valueOf(0.0).equals(vo.getRemainAmount())) {
                            continue;
                        }
                        //
                        tenor = vo.getCurrTenor();
                        remain = vo.getRemainAmount();
                        date = vo.getPayDate();
                        break;
                    }
                    //
                    if (tenor > 0 && remain.compareTo(new BigDecimal("0.0")) > 0) {
                        result.put("结果", String.format("当前第 %d 期 %s 前还需还款 %.2f 元", tenor, date, remain));
                    } else {
                        result.put("结果", "已还清");
                    }
                } else {
                    result.put("结果", "用户的还款计划为空");
                }
            } else {
                result.put("结果", "没有查询到用户的还款计划");
            }
            return result;
        } catch (Exception ex) {
            logger.error("get user repayment by order error", ex);
            result.put("错误", "查询用户还款计划失败，联系管理员");
            return result;
        }
    }

    /**
     * 订单什么时候放款
     *
     * @param arguments
     * @return
     */
    public JSONObject getUserLoanTimeByOrder(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            String orderNo = checkOrderNo(arguments);
            if (StrUtil.isEmptyIfStr(orderNo)) {
                result.put("结果", "订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号");
                return result;
            }
            JSONObject jsonObject = this.gutsHubService.getLoanMakeInfoByOrder(orderNo);
            if (jsonObject != null) {
                LoanInfoVo vo = jsonObject.getObject(KEY_DATA, LoanInfoVo.class);
                if (vo != null) {
                    // 冻结
                    if (vo.getFrozenStatus() > 0) {
                        result.put("结果", "订单已冻结");
                        return result;
                    }
                    //
                    if (null == vo.getOrderStatus() && StrUtil.isBlankIfStr(vo.getLoanTime())) {
                        result.put("结果", "订单没有放款状态");
                        return result;
                    }
                    // 放款状态
                    result.put("结果", setLoanInfoResult(vo));
                } else {
                    result.put("结果", "订单的放款信息为空");
                }
            } else {
                result.put("结果", "没有查询到订单的放款信息");
            }
            return result;
        } catch (Exception ex) {
            logger.error("get user loan time by order error", ex);
            result.put("错误", "查询订单放款时间失败，联系管理员");
            return result;
        }
    }

    /**
     * 借据状态，订单什么时候结清
     *
     * @param arguments
     * @return
     */
    public JSONObject getLoanStatusByOrder(JSONObject arguments) {
        JSONObject result = new JSONObject();
        try {
            String orderNo = checkOrderNo(arguments);
            if (StrUtil.isEmptyIfStr(orderNo)) {
                result.put("结果", "订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号");
                return result;
            }
            JSONObject jsonObject = this.gutsHubService.getLoanStatusByOrder(orderNo);
            if (jsonObject != null) {
                LoanStatusVo vo = jsonObject.getObject(KEY_DATA, LoanStatusVo.class);
                if (vo != null) {
                    String message = setLoanStatusResult(vo);
                    result.put("结果", message);
                } else {
                    result.put("结果", "订单借据状态信息为空");
                }
            } else {
                result.put("结果", "没有查询到订单借据状态信息");
            }
            return result;
        } catch (Exception ex) {
            logger.error("get loan status by order error", ex);
            result.put("错误", "查询订单借据状态失败，联系管理员");
            return result;
        }
    }

    /**
     * 还款试算
     *
     * @param arguments
     * @return
     */
    public JSONObject tryOrderRepay(JSONObject arguments) {
        return tryOrder(arguments, ACTION_REPAY);
    }

    /**
     * 退款试算
     *
     * @param arguments
     * @return
     */
    public JSONObject tryOrderRefund(JSONObject arguments) {
        return tryOrder(arguments, ACTION_REFUND);
    }

    /**
     * 试算
     *
     * @param arguments
     * @param action
     * @return
     */
    public JSONObject tryOrder(JSONObject arguments, String action) {
        JSONObject result = new JSONObject();
        String actionDesc = "";
        try {
            String orderNo = checkOrderNo(arguments);
            if (StrUtil.isEmptyIfStr(orderNo)) {
                result.put("结果", "订单号 orderNo 为空或者是非正常订单，要求用户提供正确订单号");
                return result;
            }
            JSONObject jsonObject = null;
            if (ACTION_REPAY.equals(action)) {
                jsonObject = this.gutsHubService.tryOrderRepay(orderNo);
                actionDesc = "还款试算";
            } else if (ACTION_REFUND.equals(action)) {
                jsonObject = this.gutsHubService.tryOrderRefund(orderNo);
                actionDesc = "退款试算";
            }
            if (jsonObject != null) {
                TryOrderVo vo = jsonObject.getObject(KEY_DATA, TryOrderVo.class);
                if (vo != null) {
                    result.put("结果", setTryOrderResult(vo));
                } else {
                    result.put("结果", String.format("获取%s结果为空", actionDesc));
                }
            } else {
                result.put("结果", String.format("获取%s结果失败", actionDesc));
            }
            return result;
        } catch (Exception ex) {
            logger.error("try order {} error", action, ex);
            result.put("结果", String.format("%s失败，联系管理员", actionDesc));
            return result;
        }
    }

    @NotNull
    private static String setLoanStatusResult(LoanStatusVo vo) {
        String message = "";
        message += " 借据状态：" + vo.getLoanStatus();
        if (LoanCertificateStatusEnum.退货完成.name().equals(vo.getLoanStatus()) ||
                LoanCertificateStatusEnum.提前还款完成.name().equals(vo.getLoanStatus()) ||
                LoanCertificateStatusEnum.完成.name().equals(vo.getLoanStatus())) {
            message += " 订单已结清";
        } else {
            message += " 订单未结清";
        }
        return message;
    }

    @NotNull
    private static String setLoanInfoResult(LoanInfoVo vo) {
        String result = "";
        String statusDesc = LoanOrderStatusEnum.getStatusDesc(vo.getOrderStatus());
        String loanTime = vo.getLoanTime();
        String loanApplyTime = vo.getLoanApplyTime();
        String message = vo.getMessage();
        // 未放款
        result += " 放款状态：" + statusDesc;
        if (StrUtil.isEmptyIfStr(loanTime)) {
            if (!StrUtil.isEmptyIfStr(loanApplyTime)) {
                result += " 申请时间：" + loanApplyTime;
            }
        } else {
            result += " 放款时间：" + loanTime;
        }
        if (!StrUtil.isEmptyIfStr(message)) {
            result += " 消息：" + message;
        }
        return result;
    }

    @NotNull
    private static String setTryOrderResult(TryOrderVo vo) {
        BigDecimal amount = vo.getAmount();
        String payDate = vo.getPayDate();
        DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        return amount + " 截止" + LocalDateTime.parse(payDate, parseFormatter).format(dateTimeFormatter);
    }

    private static String checkOrderNo(JSONObject arguments) {
        if (arguments.containsKey(KEY_ORDER_NO)) {
            String orderNo = arguments.getString(KEY_ORDER_NO);
            String reg = "^[A-Z]{3}[0-9]{2}-[0-9]{6}-[0-9]{6}$";
            if (orderNo.matches(reg)) {
                return orderNo;
            }
        }
        return null;
    }
}
