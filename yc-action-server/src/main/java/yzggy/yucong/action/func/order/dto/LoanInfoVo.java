package yzggy.yucong.action.func.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yamath
 * @since 2023/8/10 11:21
 */
public class LoanInfoVo implements Serializable {

    private String applySerno;
    private String appId;
    private String name;
    private int age;
    private String fundId;
    private String fundName;
    private String storeName;
    private String storeBankAccountNum;
    private int merchantDiscount;
    private BigDecimal percentUser;
    private BigDecimal percentMerchant;
    private String orderStatus; // 订单状态 待分配，待发送，待放款，放款成功，放款失败
    private String orderAuditTime;
    private String orderAuditExplain;
    private BigDecimal loanAmount; // 订单申请金额
    private BigDecimal realLoanAmount; // 资方实际放款金额
    private int tenor;
    private String loanApplyTime; // 放款申请时间 = （页面发送时间）
    private String loanTime; // 放款时间 = （页面放款通知时间），放款成功状态
    private String remark; // 备注
    private String message; // 放款原因 = （页面消息）
    private String merchantLevel;
    private int frozenStatus; // 冻结状态， 此时 status 最多到待发送
    private String frozenReason; // 冻结原因
    private String industryName;
    private String externalFundApplySerno;
    private String fundLoanNo;
    private String realLoanTime; // 起息日
    private String remarkTime;
    private String loanAccountName;
    private String canAvailableFund;
    private String financeProductCode;
    private String gltongFlag;

    public String getApplySerno() {
        return applySerno;
    }

    public void setApplySerno(String applySerno) {
        this.applySerno = applySerno;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFundId() {
        return fundId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreBankAccountNum() {
        return storeBankAccountNum;
    }

    public void setStoreBankAccountNum(String storeBankAccountNum) {
        this.storeBankAccountNum = storeBankAccountNum;
    }

    public int getMerchantDiscount() {
        return merchantDiscount;
    }

    public void setMerchantDiscount(int merchantDiscount) {
        this.merchantDiscount = merchantDiscount;
    }

    public BigDecimal getPercentUser() {
        return percentUser;
    }

    public void setPercentUser(BigDecimal percentUser) {
        this.percentUser = percentUser;
    }

    public BigDecimal getPercentMerchant() {
        return percentMerchant;
    }

    public void setPercentMerchant(BigDecimal percentMerchant) {
        this.percentMerchant = percentMerchant;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderAuditTime() {
        return orderAuditTime;
    }

    public void setOrderAuditTime(String orderAuditTime) {
        this.orderAuditTime = orderAuditTime;
    }

    public String getOrderAuditExplain() {
        return orderAuditExplain;
    }

    public void setOrderAuditExplain(String orderAuditExplain) {
        this.orderAuditExplain = orderAuditExplain;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getRealLoanAmount() {
        return realLoanAmount;
    }

    public void setRealLoanAmount(BigDecimal realLoanAmount) {
        this.realLoanAmount = realLoanAmount;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public String getLoanApplyTime() {
        return loanApplyTime;
    }

    public void setLoanApplyTime(String loanApplyTime) {
        this.loanApplyTime = loanApplyTime;
    }

    public String getLoanTime() {
        return loanTime;
    }

    public void setLoanTime(String loanTime) {
        this.loanTime = loanTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMerchantLevel() {
        return merchantLevel;
    }

    public void setMerchantLevel(String merchantLevel) {
        this.merchantLevel = merchantLevel;
    }

    public int getFrozenStatus() {
        return frozenStatus;
    }

    public void setFrozenStatus(int frozenStatus) {
        this.frozenStatus = frozenStatus;
    }

    public String getFrozenReason() {
        return frozenReason;
    }

    public void setFrozenReason(String frozenReason) {
        this.frozenReason = frozenReason;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getExternalFundApplySerno() {
        return externalFundApplySerno;
    }

    public void setExternalFundApplySerno(String externalFundApplySerno) {
        this.externalFundApplySerno = externalFundApplySerno;
    }

    public String getFundLoanNo() {
        return fundLoanNo;
    }

    public void setFundLoanNo(String fundLoanNo) {
        this.fundLoanNo = fundLoanNo;
    }

    public String getRealLoanTime() {
        return realLoanTime;
    }

    public void setRealLoanTime(String realLoanTime) {
        this.realLoanTime = realLoanTime;
    }

    public String getRemarkTime() {
        return remarkTime;
    }

    public void setRemarkTime(String remarkTime) {
        this.remarkTime = remarkTime;
    }

    public String getLoanAccountName() {
        return loanAccountName;
    }

    public void setLoanAccountName(String loanAccountName) {
        this.loanAccountName = loanAccountName;
    }

    public String getCanAvailableFund() {
        return canAvailableFund;
    }

    public void setCanAvailableFund(String canAvailableFund) {
        this.canAvailableFund = canAvailableFund;
    }

    public String getFinanceProductCode() {
        return financeProductCode;
    }

    public void setFinanceProductCode(String financeProductCode) {
        this.financeProductCode = financeProductCode;
    }

    public String getGltongFlag() {
        return gltongFlag;
    }

    public void setGltongFlag(String gltongFlag) {
        this.gltongFlag = gltongFlag;
    }

    @Override
    public String toString() {
        return "LoanInfoVo{" +
                "applySerno='" + applySerno + '\'' +
                ", appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", fundId='" + fundId + '\'' +
                ", fundName='" + fundName + '\'' +
                ", storeName='" + storeName + '\'' +
                ", storeBankAccountNum='" + storeBankAccountNum + '\'' +
                ", merchantDiscount=" + merchantDiscount +
                ", percentUser=" + percentUser +
                ", percentMerchant=" + percentMerchant +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderAuditTime='" + orderAuditTime + '\'' +
                ", orderAuditExplain='" + orderAuditExplain + '\'' +
                ", loanAmount=" + loanAmount +
                ", realLoanAmount=" + realLoanAmount +
                ", tenor=" + tenor +
                ", loanApplyTime='" + loanApplyTime + '\'' +
                ", loanTime='" + loanTime + '\'' +
                ", remark='" + remark + '\'' +
                ", message='" + message + '\'' +
                ", merchantLevel='" + merchantLevel + '\'' +
                ", frozenStatus=" + frozenStatus +
                ", frozenReason='" + frozenReason + '\'' +
                ", industryName='" + industryName + '\'' +
                ", externalFundApplySerno='" + externalFundApplySerno + '\'' +
                ", fundLoanNo='" + fundLoanNo + '\'' +
                ", realLoanTime='" + realLoanTime + '\'' +
                ", remarkTime='" + remarkTime + '\'' +
                ", loanAccountName='" + loanAccountName + '\'' +
                ", canAvailableFund='" + canAvailableFund + '\'' +
                ", financeProductCode='" + financeProductCode + '\'' +
                ", gltongFlag='" + gltongFlag + '\'' +
                '}';
    }
}
