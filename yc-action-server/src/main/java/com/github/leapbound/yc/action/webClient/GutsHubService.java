package com.github.leapbound.yc.action.webClient;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author yamath
 * @since 2023/7/17 10:31
 */
@HttpExchange
public interface GutsHubService {

    @PostExchange("/alpha/bd/account/close")
    JSONObject closeSalesAccount(@RequestParam("name") String name,
                                 @RequestParam("ldap") String ldap);

    @PostExchange("/alpha/order/repay/plan")
    JSONObject getRepayPlanByOrder(@RequestParam("orderNo") String orderNo);

    @PostExchange("/alpha/order/loan/make/info")
    JSONObject getLoanMakeInfoByOrder(@RequestParam("orderNo") String orderNo);

    @PostExchange("/alpha/order/loan/status")
    JSONObject getLoanStatusByOrder(@RequestParam("orderNo") String orderNo);

    @PostExchange("/alpha/order/repay")
    JSONObject tryOrderRepay(@RequestParam("orderNo") String orderNo);

    @PostExchange("/alpha/order/refund")
    JSONObject tryOrderRefund(@RequestParam("orderNo") String orderNo);

    @PostExchange("/cash/loan/apply/submit")
    JSONObject applySubmit(@RequestParam("name") String name,
                           @RequestParam("idNo") String idNo,
                           @RequestParam("mobile") String mobile,
                           @RequestParam("accountId") String accountId);

    @PostExchange("/cash/loan/apply/audit/status")
    JSONObject applyAuditStatus(@RequestParam("accountId") String accountId);

    @PostExchange("/cash/loan/bank/card/bind")
    JSONObject bindBankCard(@RequestParam("accountId") String accountId,
                            @RequestParam("cardNo") String cardNo,
                            @RequestParam("reserveMobile") String reserveMobile);

    @PostExchange("/cash/loan/bank/card/bind/captcha")
    JSONObject bindBankCardCaptcha(@RequestParam("accountId") String accountId,
                                   @RequestParam("verifyCode") String verifyCode);

    @PostExchange("/cash/loan/loan/submit")
    JSONObject loanSubmit(@RequestParam("accountId") String accountId,
                          @RequestParam("loanAmt") Integer loanAmt,
                          @RequestParam("period") Integer period);
}
