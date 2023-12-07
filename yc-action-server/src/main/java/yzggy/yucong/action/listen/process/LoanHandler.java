package yzggy.yucong.action.listen.process;

import com.alibaba.fastjson.JSONObject;
import org.camunda.bpm.client.spring.SpringTopicSubscription;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.spring.event.SubscriptionInitializedEvent;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Map;

@Configuration
public class LoanHandler {
    private static final Logger log = LoggerFactory.getLogger(LoanHandler.class);

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
            // todo send login sms
            JSONObject inputForm = new JSONObject();
            inputForm.put("z_sendUserMobileVerifyCode", true);

            // 通知用户
            // todo notify user

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
            // certed需要验证字段
            // todo load identify

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
            // todo get app token
            // 限额校验
            //todo check bank card
            // 历史数据校验
            // todo check exist user info
            // 协议支付签约校验
            // todo check user pay protocol
            JSONObject checkProtocolResult = new JSONObject();
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

            // todo get app token

            JSONObject info = new JSONObject();
            info.put("C_APP_ID", appId);
            info.put("C_STEP_ID", "NYB01_02");
            info.put("C_DEVICE_TYPE", "bot");
            info.put("C_FORM_ID", "NYB01");
            // todo submit apply step

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

            // todo get app token

            JSONObject info = new JSONObject();
            info.put("C_APP_ID", appId);
            info.put("C_STEP_ID", "PREVIEW");
            // todo submit apply step

            externalTaskService.complete(externalTask, null);
        };
    }
}
