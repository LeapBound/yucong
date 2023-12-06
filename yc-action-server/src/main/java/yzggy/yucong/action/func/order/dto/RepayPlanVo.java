package yzggy.yucong.action.func.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yamath
 * @since 2023/8/10 10:06
 */
public class RepayPlanVo implements Serializable {

    private String planNo;

    private int currTenor;

    private String payDate;

    private String finishDate;

    private BigDecimal allPayAmount;

    private BigDecimal allPaidAmount;

    private BigDecimal remitAmount;

    private BigDecimal adjAmount;

    private BigDecimal remainAmount;

    private String status;

    private BigDecimal allPayCorpus;

    private BigDecimal allPayFee;

    private BigDecimal allPayLateFee;

    private BigDecimal allPayPenalties;

    private BigDecimal allPayOtherFee;

    private BigDecimal allPayRepayAllFee;

    private BigDecimal allPaidCorpus;

    private BigDecimal allPaidFee;

    private BigDecimal allPaidLateFee;

    private BigDecimal allPaidPenalties;

    private BigDecimal allPaidOtherFee;

    private BigDecimal allPaidRepayAllFee;

    private BigDecimal currPayLateFee;

    private BigDecimal currPayPenalties;

    public String getPlanNo() {
        return planNo;
    }

    public void setPlanNo(String planNo) {
        this.planNo = planNo;
    }

    public int getCurrTenor() {
        return currTenor;
    }

    public void setCurrTenor(int currTenor) {
        this.currTenor = currTenor;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public BigDecimal getAllPayAmount() {
        return allPayAmount;
    }

    public void setAllPayAmount(BigDecimal allPayAmount) {
        this.allPayAmount = allPayAmount;
    }

    public BigDecimal getAllPaidAmount() {
        return allPaidAmount;
    }

    public void setAllPaidAmount(BigDecimal allPaidAmount) {
        this.allPaidAmount = allPaidAmount;
    }

    public BigDecimal getRemitAmount() {
        return remitAmount;
    }

    public void setRemitAmount(BigDecimal remitAmount) {
        this.remitAmount = remitAmount;
    }

    public BigDecimal getAdjAmount() {
        return adjAmount;
    }

    public void setAdjAmount(BigDecimal adjAmount) {
        this.adjAmount = adjAmount;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAllPayCorpus() {
        return allPayCorpus;
    }

    public void setAllPayCorpus(BigDecimal allPayCorpus) {
        this.allPayCorpus = allPayCorpus;
    }

    public BigDecimal getAllPayFee() {
        return allPayFee;
    }

    public void setAllPayFee(BigDecimal allPayFee) {
        this.allPayFee = allPayFee;
    }

    public BigDecimal getAllPayLateFee() {
        return allPayLateFee;
    }

    public void setAllPayLateFee(BigDecimal allPayLateFee) {
        this.allPayLateFee = allPayLateFee;
    }

    public BigDecimal getAllPayPenalties() {
        return allPayPenalties;
    }

    public void setAllPayPenalties(BigDecimal allPayPenalties) {
        this.allPayPenalties = allPayPenalties;
    }

    public BigDecimal getAllPayOtherFee() {
        return allPayOtherFee;
    }

    public void setAllPayOtherFee(BigDecimal allPayOtherFee) {
        this.allPayOtherFee = allPayOtherFee;
    }

    public BigDecimal getAllPayRepayAllFee() {
        return allPayRepayAllFee;
    }

    public void setAllPayRepayAllFee(BigDecimal allPayRepayAllFee) {
        this.allPayRepayAllFee = allPayRepayAllFee;
    }

    public BigDecimal getAllPaidCorpus() {
        return allPaidCorpus;
    }

    public void setAllPaidCorpus(BigDecimal allPaidCorpus) {
        this.allPaidCorpus = allPaidCorpus;
    }

    public BigDecimal getAllPaidFee() {
        return allPaidFee;
    }

    public void setAllPaidFee(BigDecimal allPaidFee) {
        this.allPaidFee = allPaidFee;
    }

    public BigDecimal getAllPaidLateFee() {
        return allPaidLateFee;
    }

    public void setAllPaidLateFee(BigDecimal allPaidLateFee) {
        this.allPaidLateFee = allPaidLateFee;
    }

    public BigDecimal getAllPaidPenalties() {
        return allPaidPenalties;
    }

    public void setAllPaidPenalties(BigDecimal allPaidPenalties) {
        this.allPaidPenalties = allPaidPenalties;
    }

    public BigDecimal getAllPaidOtherFee() {
        return allPaidOtherFee;
    }

    public void setAllPaidOtherFee(BigDecimal allPaidOtherFee) {
        this.allPaidOtherFee = allPaidOtherFee;
    }

    public BigDecimal getAllPaidRepayAllFee() {
        return allPaidRepayAllFee;
    }

    public void setAllPaidRepayAllFee(BigDecimal allPaidRepayAllFee) {
        this.allPaidRepayAllFee = allPaidRepayAllFee;
    }

    public BigDecimal getCurrPayLateFee() {
        return currPayLateFee;
    }

    public void setCurrPayLateFee(BigDecimal currPayLateFee) {
        this.currPayLateFee = currPayLateFee;
    }

    public BigDecimal getCurrPayPenalties() {
        return currPayPenalties;
    }

    public void setCurrPayPenalties(BigDecimal currPayPenalties) {
        this.currPayPenalties = currPayPenalties;
    }

    @Override
    public String toString() {
        return "RepayPlanVo{" +
                "planNo='" + planNo + '\'' +
                ", currTenor=" + currTenor +
                ", payDate='" + payDate + '\'' +
                ", finishDate='" + finishDate + '\'' +
                ", allPayAmount=" + allPayAmount +
                ", allPaidAmount=" + allPaidAmount +
                ", remitAmount=" + remitAmount +
                ", adjAmount=" + adjAmount +
                ", remainAmount=" + remainAmount +
                ", status='" + status + '\'' +
                ", allPayCorpus=" + allPayCorpus +
                ", allPayFee=" + allPayFee +
                ", allPayLateFee=" + allPayLateFee +
                ", allPayPenalties=" + allPayPenalties +
                ", allPayOtherFee=" + allPayOtherFee +
                ", allPayRepayAllFee=" + allPayRepayAllFee +
                ", allPaidCorpus=" + allPaidCorpus +
                ", allPaidFee=" + allPaidFee +
                ", allPaidLateFee=" + allPaidLateFee +
                ", allPaidPenalties=" + allPaidPenalties +
                ", allPaidOtherFee=" + allPaidOtherFee +
                ", allPaidRepayAllFee=" + allPaidRepayAllFee +
                ", currPayLateFee=" + currPayLateFee +
                ", currPayPenalties=" + currPayPenalties +
                '}';
    }
}
