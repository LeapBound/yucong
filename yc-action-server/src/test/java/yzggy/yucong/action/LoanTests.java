package yzggy.yucong.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unfbx.chatgpt.entity.chat.Message;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import yzggy.yucong.action.controller.YcFunctionOpenaiController;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRequest;

import java.util.HashMap;

@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoanTests {
    private static final Logger log = LoggerFactory.getLogger(LoanTests.class);

    private final String accountId = "account001";
    private final String deviceId = "deviceId001";
    private final String mobile = "15913175491";
    private final String mobileVerifyCode = "123456";
    private final String salesMobile = "13818634281";
    private final Integer amount = 11300;
    private final String productName = "刮腿毛";
    private final String loanTerm = "保理单单过-3+9 1%-1%-DDG";
    private final String idFront = "https://beta.geexfinance.com/front-api/geex_gank/user/showImg?img=group2/M00/A6/1C/wKhvEmVLWQ2AEAMzAABDtm4yGxw824.jpg";
    private final String idBack = "https://beta.geexfinance.com/front-api/geex_gank/user/showImg?img=group2/M00/A6/1C/wKhvEmVLWQ6AD7nmAABGBGy7k88294.jpg";
    private final String bankCard = "5522455949692150";
    private final String bankMobile = "15913175492";
    private final String payProtocolVerifyCode = "123456";
    private final String maritalStatus = "01";
    private final String mailAddr = "居住详细地址";
    private final String companyName = "未获取到工作单位";

    @Autowired
    private YcFunctionOpenaiController ycFunctionOpenaiController;

    @Test
    void testExternalTask() {
        deleteLoanProcess();
        startLoan();
        bindMobile();
    }

    @Test
    @Order(1)
    void deleteLoanProcess() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        functionExecuteRequest.setName("delete_loan_process");
        functionExecuteRequest.setArguments(JSON.toJSONString(new HashMap<>(0)));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("删除流程 {}", result);
    }

    @Test
    @Order(2)
    void startLoan() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        functionExecuteRequest.setName("apply_loan");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("发起进件 {}", result);
    }

    @Test
    @Order(3)
    void bindMobile() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("mobile", this.mobile);

        functionExecuteRequest.setName("bind_mobile");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("用户提供手机号 {}", result);
    }

    @Test
    @Order(4)
    void verifyMobileCode() throws InterruptedException {
        Thread.sleep(500);
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);
        mockHttpServletRequest.addHeader("deviceId", this.deviceId);

        JSONObject arguments = new JSONObject();
        arguments.put("smsCode", this.mobileVerifyCode);

        functionExecuteRequest.setName("verify_mobile_code");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("验证用户手机号 {}", result);

        if (result.getContent().startsWith("执行方法没有结果返回")) {
            Thread.sleep(3000);
            verifyMobileCode();
        }
    }

    @Test
    @Order(5)
    void loadLoanConfigWithBDMobile() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("bdMobile", this.salesMobile);

        functionExecuteRequest.setName("config_by_bd_mobile");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("根据BD手机号获取进件配置 {}", result);
    }

    @Test
    @Order(6)
    void inputLoanInfo() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("productName", this.productName);
        arguments.put("productAmount", this.amount);

        functionExecuteRequest.setName("product_info");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("用户提供贷款项目和金额 {}", result);
    }

    @Test
    @Order(7)
    void loanTerm() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("loanTerm", this.loanTerm);

        functionExecuteRequest.setName("loan_term");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("用户提供期数 {}", result);
    }

    @Test
    @Order(8)
    void idFront() throws InterruptedException {
        Thread.sleep(500);
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("idPhotoType", "idnoFront");
        arguments.put("idPhotoUrl", this.idFront);

        functionExecuteRequest.setName("id_photo_front");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("身份证头像面 {}", result);

        if (result.getContent().startsWith("执行方法没有结果返回")) {
            idFront();
        }
    }

    @Test
    @Order(9)
    void idBack() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("idPhotoType", "idnoBack");
        arguments.put("idPhotoUrl", this.idBack);

        functionExecuteRequest.setName("id_photo_back");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("身份证国徽面 {}", result);
    }

    @Test
    @Order(10)
    void bankCard() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("bankCard", this.bankCard);
        arguments.put("bankMobile", this.bankMobile);

        functionExecuteRequest.setName("bank_card");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("用户提供银行卡 {}", result);
    }

    @Test
    @Order(11)
    void submitPayProtocol() throws InterruptedException {
        Thread.sleep(500);
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("payProtocolVerifyCode", this.payProtocolVerifyCode);

        functionExecuteRequest.setName("submit_pay_protocol");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("协议签约 {}", result);

        if (result.getContent().startsWith("执行方法没有结果返回")) {
            Thread.sleep(3000);
            submitPayProtocol();
        }
    }

    @Test
    @Order(12)
    void thirdStep() throws InterruptedException {
        Thread.sleep(500);
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("maritalStatus", this.maritalStatus);

        functionExecuteRequest.setName("third_step");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("第三步提交 {}", result);

        if (result.getContent().startsWith("执行方法没有结果返回")) {
            Thread.sleep(3000);
            thirdStep();
        }
    }

    @Test
    @Order(13)
    void forthStep() {
        FunctionExecuteRequest functionExecuteRequest = new FunctionExecuteRequest();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("accountId", this.accountId);

        JSONObject arguments = new JSONObject();
        arguments.put("companyName", this.companyName);
        arguments.put("mailAddr", this.mailAddr);

        functionExecuteRequest.setName("forth_step");
        functionExecuteRequest.setArguments(JSON.toJSONString(arguments));

        Message result = this.ycFunctionOpenaiController.executeFunction(functionExecuteRequest, mockHttpServletRequest);
        log.info("第四步提交 {}", result);
    }

}
