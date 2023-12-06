package yzggy.yucong.action.listen.process;

import com.alibaba.fastjson.JSONObject;
import geex.architecture.guts.hub.func.loan.service.LoanService;
import geex.architecture.guts.hub.func.loan.service.ProcessService;
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

    private final LoanService loanService;
    private final ProcessService processService;

    public LoanHandler(LoanService loanService, ProcessService processService) {
        this.loanService = loanService;
        this.processService = processService;
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
            this.loanService.sendLoginSms(mobile);
            JSONObject inputForm = new JSONObject();
            inputForm.put("z_sendUserMobileVerifyCode", true);

            // 通知用户
            this.loanService.notifyUser("account001", "验证码已经发送，请在收到验证码后发送给我");

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
            this.loanService.loadIdentity(this.loanService.getAppToken(mobile), appId);

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
            String appToken = this.loanService.getAppToken(mobile);
            // 限额校验
            this.loanService.bankCardCheck(appToken, name, idNo, bankCard, amount);
            // 历史数据校验
            this.loanService.checkExistsUserInfo(appToken, name, idNo);
            // 协议支付签约校验
            JSONObject checkProtocolResult = this.loanService.checkUserPayProtocol(appToken, appId, name, idNo, bankCard, bankMobile, bankCode);
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

            String appToken = this.loanService.getAppToken(mobile);

            JSONObject info = new JSONObject();
            info.put("C_APP_ID", appId);
            info.put("C_STEP_ID", "NYB01_02");
            info.put("C_DEVICE_TYPE", "bot");
            info.put("C_FORM_ID", "NYB01");
            this.loanService.submitApplyStep(appToken, info);

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

            String appToken = this.loanService.getAppToken(mobile);

            JSONObject info = new JSONObject();
            info.put("C_APP_ID", appId);
            info.put("C_STEP_ID", "PREVIEW");
            this.loanService.submitApplyStep(appToken, info);

            externalTaskService.complete(externalTask, null);
        };
    }
}
