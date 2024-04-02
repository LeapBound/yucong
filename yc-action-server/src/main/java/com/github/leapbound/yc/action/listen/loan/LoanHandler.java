package com.github.leapbound.yc.action.listen.loan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRequest;
import com.github.leapbound.yc.action.service.YcFunctionOpenaiService;
import org.camunda.bpm.client.spring.SpringTopicSubscription;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.spring.event.SubscriptionInitializedEvent;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yamath
 * @since 2024/4/1 9:10
 */
@Component
public class LoanHandler {

    private static final Logger log = LoggerFactory.getLogger(LoanHandler.class);

    private final YcFunctionOpenaiService ycFunctionOpenaiService;

    public LoanHandler(YcFunctionOpenaiService ycFunctionOpenaiService) {
        this.ycFunctionOpenaiService = ycFunctionOpenaiService;
    }

    @EventListener(SubscriptionInitializedEvent.class)
    public void catchSubscriptionInitEvent(SubscriptionInitializedEvent event) {
        SpringTopicSubscription topicSubscription = event.getSource();
        log.info("Subscription with topic  {}", topicSubscription.getTopicName());
    }

    @Bean
    @ExternalTaskSubscription("sendClientMobileVerifyCode")
    public ExternalTaskHandler sendClientMobileVerifyCode() {
        return (externalTask, externalTaskService) -> {
            log.info("sendClientMobileVerifyCode");

            String mobile = externalTask.getVariable("mobile");
            // send login sms
            JSONObject args = new JSONObject() {{
                put("userMobile", mobile);
            }};
            this.execute("send_login_sms", args);
            // 通知用户
            args = new JSONObject() {{
                put("userId", mobile);
                put("content", "验证码已经发送，请在收到验证码后发送给我");
            }};
            this.execute("notify_user", args);
            //
            JSONObject inputForm = new JSONObject();
            inputForm.put("z_sendUserMobileVerifyCode", true);

            externalTaskService.complete(externalTask, inputForm);
        };
    }

    @Bean
    @ExternalTaskSubscription("loadClientIdentity")
    public ExternalTaskHandler loadClientIdentity() {
        return (externalTask, externalTaskService) -> {
            log.info("loadClientIdentity");

            String mobile = externalTask.getVariable("mobile");
            String appId = externalTask.getVariable("appId");
            JSONObject args = new JSONObject() {{
                put("userMobile", mobile);
            }};
            String appToken = this.execute("get_app_token", args).getString("token");
            // certed需要验证字段
            args = new JSONObject() {{
                put("appId", appId);
                put("token", appToken);
            }};
            this.execute("load_identity", args);

            externalTaskService.complete(externalTask, null);
        };
    }

    @Bean
    @ExternalTaskSubscription("bankCardCheck")
    public ExternalTaskHandler bankCardCheck() {
        return (externalTask, externalTaskService) -> {
            log.info("bankCardCheck");

            String appId = externalTask.getVariable("appId");
            String mobile = externalTask.getVariable("mobile");
            Integer amount = externalTask.getVariable("applyAmount");
            String bankCard = externalTask.getVariable("bankCard");
            String bankMobile = externalTask.getVariable("bankMobile");
            String bankCode = externalTask.getVariable("bankCode");

            Map<String, Object> ocrFront = externalTask.getVariable("ocridnoFront");
            Map<String, Object> ocrFrontDetail = (Map<String, Object>) ocrFront.get("ocrDetail");
            String name = (String) ocrFrontDetail.get("name");
            String idNo = (String) ocrFrontDetail.get("idCardNumber");
            Map<String, Object> ocrBack = externalTask.getVariable("ocridnoBack");
            Map<String, Object> ocrBackDetail = (Map<String, Object>) ocrBack.get("ocrDetail");
            String idValid = (String) ocrBackDetail.get("validDate");

            JSONObject inputForm = new JSONObject();
            //
            JSONObject args = new JSONObject() {{
                put("userMobile", mobile);
            }};
            String appToken = this.execute("get_app_token", args).getString("token");
            // 限额校验
            args = new JSONObject() {{
                put("token", appToken);
                put("name", name);
                put("idNo", idNo);
                put("bankCard", bankCard);
                put("amount", amount);
            }};
            this.execute("check_bankCard_limit", args).getString("result");
            // 历史数据校验
            args = new JSONObject() {{
                put("token", appToken);
                put("name", name);
                put("idNo", idNo);
            }};
            this.execute("check_old_identity", args);
            // 协议支付签约校验
            args = new JSONObject() {{
                put("token", appToken);
                put("appId", appId);
                put("name", name);
                put("idNo", idNo);
                put("bankCard", bankCard);
                put("bankMobile", bankMobile);
                put("bankCode", bankCode);
            }};
            JSONObject checkProtocolResult = this.execute("check_userPay_protocol", args);
            inputForm.put("payProtocolKey", checkProtocolResult.getString("makeProtocolKey"));

            externalTaskService.complete(externalTask, inputForm);
        };
    }

    @Bean
    @ExternalTaskSubscription("secondStep")
    public ExternalTaskHandler secondStep() {
        return (externalTask, externalTaskService) -> {
            log.info("secondStep");

            String appId = externalTask.getVariable("appId");
            String mobile = externalTask.getVariable("mobile");
            JSONObject args = new JSONObject() {{
                put("userMobile", mobile);
            }};
            String appToken = this.execute("get_app_token", args).getString("token");
            //
            JSONObject info = new JSONObject();
            info.put("C_APP_ID", appId);
            info.put("C_STEP_ID", "NYB01_02");
            info.put("C_DEVICE_TYPE", "bot");
            info.put("C_FORM_ID", "NYB01");
            //
            args = new JSONObject() {{
                put("token", appToken);
                put("info", info);
            }};
            this.execute("submit_apply_step", args);

            externalTaskService.complete(externalTask, null);
        };
    }

    @Bean
    @ExternalTaskSubscription("submitApplyAudit")
    public ExternalTaskHandler submitApplyAudit() {
        return (externalTask, externalTaskService) -> {
            log.info("submitApplyAudit");

            String appId = externalTask.getVariable("appId");
            String mobile = externalTask.getVariable("mobile");
            JSONObject args = new JSONObject() {{
                put("userMobile", mobile);
            }};
            String appToken = this.execute("get_app_token", args).getString("token");

            JSONObject info = new JSONObject();
            info.put("C_APP_ID", appId);
            info.put("C_STEP_ID", "PREVIEW");
            args = new JSONObject() {{
                put("token", appToken);
                put("info", info);
            }};
            this.execute("submit_apply_step", args);

            externalTaskService.complete(externalTask, null);
        };
    }

    private JSONObject execute(String function, JSONObject args) {
        FunctionExecuteRequest request = new FunctionExecuteRequest();
        request.setName(function);
        request.setArguments(JSON.toJSONString(args));
        return this.ycFunctionOpenaiService.executeGroovy(request);
    }
}
