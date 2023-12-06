package yzggy.yucong.action.func.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yamath
 * @since 2023/8/10 15:42
 */
public class TryOrderVo implements Serializable {

    private Boolean success;
    private String errorCode;
    private String errorMessage;
    private BigDecimal amount;
    private BigDecimal corpus;
    private BigDecimal fee;
    private BigDecimal lateFee;
    private BigDecimal penalties;
    private BigDecimal otherFee;
    private BigDecimal refundFee;
    private BigDecimal merchantRefundFee;
    private BigDecimal needAllPayFee;
    private BigDecimal allPaidFee;
    private String payDate;
    private BigDecimal backCorpus;
    private BigDecimal backFee;
    private BigDecimal assFee;
    private String financeProductCode;
    private BigDecimal remainMerchantFee;
    private BigDecimal serviceCharge;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCorpus() {
        return corpus;
    }

    public void setCorpus(BigDecimal corpus) {
        this.corpus = corpus;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public BigDecimal getPenalties() {
        return penalties;
    }

    public void setPenalties(BigDecimal penalties) {
        this.penalties = penalties;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(BigDecimal refundFee) {
        this.refundFee = refundFee;
    }

    public BigDecimal getMerchantRefundFee() {
        return merchantRefundFee;
    }

    public void setMerchantRefundFee(BigDecimal merchantRefundFee) {
        this.merchantRefundFee = merchantRefundFee;
    }

    public BigDecimal getNeedAllPayFee() {
        return needAllPayFee;
    }

    public void setNeedAllPayFee(BigDecimal needAllPayFee) {
        this.needAllPayFee = needAllPayFee;
    }

    public BigDecimal getAllPaidFee() {
        return allPaidFee;
    }

    public void setAllPaidFee(BigDecimal allPaidFee) {
        this.allPaidFee = allPaidFee;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getBackCorpus() {
        return backCorpus;
    }

    public void setBackCorpus(BigDecimal backCorpus) {
        this.backCorpus = backCorpus;
    }

    public BigDecimal getBackFee() {
        return backFee;
    }

    public void setBackFee(BigDecimal backFee) {
        this.backFee = backFee;
    }

    public BigDecimal getAssFee() {
        return assFee;
    }

    public void setAssFee(BigDecimal assFee) {
        this.assFee = assFee;
    }

    public String getFinanceProductCode() {
        return financeProductCode;
    }

    public void setFinanceProductCode(String financeProductCode) {
        this.financeProductCode = financeProductCode;
    }

    public BigDecimal getRemainMerchantFee() {
        return remainMerchantFee;
    }

    public void setRemainMerchantFee(BigDecimal remainMerchantFee) {
        this.remainMerchantFee = remainMerchantFee;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    @Override
    public String toString() {
        return "LoanTryVo{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", amount=" + amount +
                ", corpus=" + corpus +
                ", fee=" + fee +
                ", lateFee=" + lateFee +
                ", penalties=" + penalties +
                ", otherFee=" + otherFee +
                ", refundFee=" + refundFee +
                ", merchantRefundFee=" + merchantRefundFee +
                ", needAllPayFee=" + needAllPayFee +
                ", allPaidFee=" + allPaidFee +
                ", payDate='" + payDate + '\'' +
                ", backCorpus=" + backCorpus +
                ", backFee=" + backFee +
                ", assFee=" + assFee +
                ", financeProductCode='" + financeProductCode + '\'' +
                ", remainMerchantFee=" + remainMerchantFee +
                ", serviceCharge=" + serviceCharge +
                '}';
    }
}
