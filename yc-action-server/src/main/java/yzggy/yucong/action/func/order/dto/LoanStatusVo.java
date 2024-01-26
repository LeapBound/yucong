package yzggy.yucong.action.func.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yamath
 * @since 2023/8/10 14:37
 */
public class LoanStatusVo implements Serializable {
    private int contractId;
    private String contractNo;
    private String extId;
    private String contractExtId;
    private String loanNo;
    private String loanStatus;
    private int userId;
    private String idNo;
    private String userName;
    private int userAge;
    private String userAddress;
    private String homeCity;
    private String companyAddress;
    private String companyName;
    private String mobile;
    private String merchantCode;
    private String merchantName;
    private String storeCode;
    private String storeName;
    private String itemName;
    private String loanDate;
    private BigDecimal loanAmount;
    private BigDecimal merchantAmount;
    private BigDecimal merchantFee;
    private BigDecimal bondAmount;
    private int productId;
    private String productCode;
    private String productName;
    private int tenor;
    private String repayType;
    private int currTenor;
    private int remainTenor;
    private String isOverdue;
    private int overdueDays;
    private String overdueCode;
    private String orderOverdueSituation;
    private BigDecimal currPayAmount;
    private BigDecimal currPayCorpus;
    private BigDecimal currPayFee;
    private BigDecimal currPayLateFee;
    private BigDecimal currPayPenalties;
    private BigDecimal currPayOtherFee;
    private BigDecimal currPayServiceCharge;
    private BigDecimal currPaidAmount;
    private BigDecimal currPaidCorpus;
    private BigDecimal currPaidFee;
    private BigDecimal currPaidLateFee;
    private BigDecimal currPaidPenalties;
    private BigDecimal currPaidOtherFee;
    private BigDecimal currPaidServiceCharge;
    private BigDecimal allPayAmount;
    private BigDecimal allPayCorpus;
    private BigDecimal allPayFee;
    private BigDecimal allPayLateFee;
    private BigDecimal allPayPenalties;
    private BigDecimal allPayOtherFee;
    private BigDecimal allPayServiceCharge;
    private BigDecimal allPaidAmount;
    private BigDecimal allPaidCorpus;
    private BigDecimal allPaidFee;
    private BigDecimal allPaidLateFee;
    private BigDecimal allPaidPenalties;
    private BigDecimal allPaidOtherFee;
    private BigDecimal allPaidServiceCharge;
    private BigDecimal remitAmount;
    private BigDecimal remitCount;
    private BigDecimal percentMerchant;
    private BigDecimal staticMerchant;
    private String baseMerchant;
    private BigDecimal percentUser;
    private BigDecimal staticUser;
    private String baseUser;
    private BigDecimal percentBond;
    private BigDecimal staticBond;
    private String baseBond;
    private BigDecimal percentLateFee;
    private BigDecimal staticLateFee;
    private BigDecimal minLateFee;
    private String baseLateFee;
    private BigDecimal percentPenalties;
    private BigDecimal staticPenalties;
    private BigDecimal minPenalties;
    private String basePenalties;
    private BigDecimal percentAllRepay;
    private BigDecimal staticAllRepay;
    private BigDecimal minAllRepay;
    private String baseAllRepay;
    private BigDecimal percentRefund;
    private BigDecimal staticRefund;
    private BigDecimal minRefund;
    private String baseRefund;
    private int graceLate;
    private int gracePenalties;
    private String payDateType;
    private int payDate;
    private String refundDay;
    private String canRepayAll;
    private int termDays;
    private int termTenor;
    private String termTime;
    private BigDecimal firstPayAmount;
    private String firstPayTime;
    private BigDecimal percentFirstPay;
    private BigDecimal staticFirstPay;
    private BigDecimal allRemainCorpus;
    private BigDecimal allRemainFee;
    private String nextPayDate;
    private String prevPayDate;
    private BigDecimal prevPayAmount;
    private BigDecimal nextPayAmount;
    private BigDecimal nextPayFee;
    private BigDecimal nextPayServiceCharge;
    private BigDecimal adjAmount;
    private BigDecimal adjCorpus;
    private BigDecimal adjFee;
    private BigDecimal adjLateFee;
    private BigDecimal adjPenalties;
    private BigDecimal adjOtherFee;
    private BigDecimal canBackCorpus;
    private BigDecimal canBackFee;
    private int aheadOfPayDays;
    private BigDecimal partRefundAmount;
    private BigDecimal remainAmount;
    private BigDecimal remainCorpus;
    private BigDecimal remainFee;
    private BigDecimal remainLateFee;
    private BigDecimal remainPenalties;
    private BigDecimal remainOtherFee;
    private BigDecimal remainServiceCharge;
    private BigDecimal percentMerchantRefund;
    private int remainPayTenor;
    private String productType;
    private BigDecimal ddgPercentFeep;
    private BigDecimal ddgPercentRiskFee;
    private int percentMerchantFlag;
    private BigDecimal originUserPercent;
    private BigDecimal originMerchantPercent;
    private String fundId;
    private BigDecimal originLoanAmount;
    private String bankCard;
    private String bankCode;
    private String assureFlag;
    private int debtFlag;
    private int repayDelay;
    private BigDecimal assureAmount;
    private String contractDueDate;
    private String firstPayDate;
    private String financeProductCode;
    private int merchantPushFlag;
    private String industryCate;
    private String industryCateCode;
    private Boolean educationFree;
    private String xjdChannelName;
    private BigDecimal borrowerBondPercent;
    private BigDecimal supplierFeePercent;
    private BigDecimal borrowerBondAmount;
    private BigDecimal supplierFeeAmount;
    private String canRenewalDate;
    private int loanFreezeFlag;
    private int cavFlag;
    private String cavDate;
    private BigDecimal borrowAvailableBondAmount;
    private int repayDateAfter;
    private String businessType;
    private String orgExtId;

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getContractExtId() {
        return contractExtId;
    }

    public void setContractExtId(String contractExtId) {
        this.contractExtId = contractExtId;
    }

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getMerchantAmount() {
        return merchantAmount;
    }

    public void setMerchantAmount(BigDecimal merchantAmount) {
        this.merchantAmount = merchantAmount;
    }

    public BigDecimal getMerchantFee() {
        return merchantFee;
    }

    public void setMerchantFee(BigDecimal merchantFee) {
        this.merchantFee = merchantFee;
    }

    public BigDecimal getBondAmount() {
        return bondAmount;
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public String getRepayType() {
        return repayType;
    }

    public void setRepayType(String repayType) {
        this.repayType = repayType;
    }

    public int getCurrTenor() {
        return currTenor;
    }

    public void setCurrTenor(int currTenor) {
        this.currTenor = currTenor;
    }

    public int getRemainTenor() {
        return remainTenor;
    }

    public void setRemainTenor(int remainTenor) {
        this.remainTenor = remainTenor;
    }

    public String getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(String isOverdue) {
        this.isOverdue = isOverdue;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getOverdueCode() {
        return overdueCode;
    }

    public void setOverdueCode(String overdueCode) {
        this.overdueCode = overdueCode;
    }

    public String getOrderOverdueSituation() {
        return orderOverdueSituation;
    }

    public void setOrderOverdueSituation(String orderOverdueSituation) {
        this.orderOverdueSituation = orderOverdueSituation;
    }

    public BigDecimal getCurrPayAmount() {
        return currPayAmount;
    }

    public void setCurrPayAmount(BigDecimal currPayAmount) {
        this.currPayAmount = currPayAmount;
    }

    public BigDecimal getCurrPayCorpus() {
        return currPayCorpus;
    }

    public void setCurrPayCorpus(BigDecimal currPayCorpus) {
        this.currPayCorpus = currPayCorpus;
    }

    public BigDecimal getCurrPayFee() {
        return currPayFee;
    }

    public void setCurrPayFee(BigDecimal currPayFee) {
        this.currPayFee = currPayFee;
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

    public BigDecimal getCurrPayOtherFee() {
        return currPayOtherFee;
    }

    public void setCurrPayOtherFee(BigDecimal currPayOtherFee) {
        this.currPayOtherFee = currPayOtherFee;
    }

    public BigDecimal getCurrPayServiceCharge() {
        return currPayServiceCharge;
    }

    public void setCurrPayServiceCharge(BigDecimal currPayServiceCharge) {
        this.currPayServiceCharge = currPayServiceCharge;
    }

    public BigDecimal getCurrPaidAmount() {
        return currPaidAmount;
    }

    public void setCurrPaidAmount(BigDecimal currPaidAmount) {
        this.currPaidAmount = currPaidAmount;
    }

    public BigDecimal getCurrPaidCorpus() {
        return currPaidCorpus;
    }

    public void setCurrPaidCorpus(BigDecimal currPaidCorpus) {
        this.currPaidCorpus = currPaidCorpus;
    }

    public BigDecimal getCurrPaidFee() {
        return currPaidFee;
    }

    public void setCurrPaidFee(BigDecimal currPaidFee) {
        this.currPaidFee = currPaidFee;
    }

    public BigDecimal getCurrPaidLateFee() {
        return currPaidLateFee;
    }

    public void setCurrPaidLateFee(BigDecimal currPaidLateFee) {
        this.currPaidLateFee = currPaidLateFee;
    }

    public BigDecimal getCurrPaidPenalties() {
        return currPaidPenalties;
    }

    public void setCurrPaidPenalties(BigDecimal currPaidPenalties) {
        this.currPaidPenalties = currPaidPenalties;
    }

    public BigDecimal getCurrPaidOtherFee() {
        return currPaidOtherFee;
    }

    public void setCurrPaidOtherFee(BigDecimal currPaidOtherFee) {
        this.currPaidOtherFee = currPaidOtherFee;
    }

    public BigDecimal getCurrPaidServiceCharge() {
        return currPaidServiceCharge;
    }

    public void setCurrPaidServiceCharge(BigDecimal currPaidServiceCharge) {
        this.currPaidServiceCharge = currPaidServiceCharge;
    }

    public BigDecimal getAllPayAmount() {
        return allPayAmount;
    }

    public void setAllPayAmount(BigDecimal allPayAmount) {
        this.allPayAmount = allPayAmount;
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

    public BigDecimal getAllPayServiceCharge() {
        return allPayServiceCharge;
    }

    public void setAllPayServiceCharge(BigDecimal allPayServiceCharge) {
        this.allPayServiceCharge = allPayServiceCharge;
    }

    public BigDecimal getAllPaidAmount() {
        return allPaidAmount;
    }

    public void setAllPaidAmount(BigDecimal allPaidAmount) {
        this.allPaidAmount = allPaidAmount;
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

    public BigDecimal getAllPaidServiceCharge() {
        return allPaidServiceCharge;
    }

    public void setAllPaidServiceCharge(BigDecimal allPaidServiceCharge) {
        this.allPaidServiceCharge = allPaidServiceCharge;
    }

    public BigDecimal getRemitAmount() {
        return remitAmount;
    }

    public void setRemitAmount(BigDecimal remitAmount) {
        this.remitAmount = remitAmount;
    }

    public BigDecimal getRemitCount() {
        return remitCount;
    }

    public void setRemitCount(BigDecimal remitCount) {
        this.remitCount = remitCount;
    }

    public BigDecimal getPercentMerchant() {
        return percentMerchant;
    }

    public void setPercentMerchant(BigDecimal percentMerchant) {
        this.percentMerchant = percentMerchant;
    }

    public BigDecimal getStaticMerchant() {
        return staticMerchant;
    }

    public void setStaticMerchant(BigDecimal staticMerchant) {
        this.staticMerchant = staticMerchant;
    }

    public String getBaseMerchant() {
        return baseMerchant;
    }

    public void setBaseMerchant(String baseMerchant) {
        this.baseMerchant = baseMerchant;
    }

    public BigDecimal getPercentUser() {
        return percentUser;
    }

    public void setPercentUser(BigDecimal percentUser) {
        this.percentUser = percentUser;
    }

    public BigDecimal getStaticUser() {
        return staticUser;
    }

    public void setStaticUser(BigDecimal staticUser) {
        this.staticUser = staticUser;
    }

    public String getBaseUser() {
        return baseUser;
    }

    public void setBaseUser(String baseUser) {
        this.baseUser = baseUser;
    }

    public BigDecimal getPercentBond() {
        return percentBond;
    }

    public void setPercentBond(BigDecimal percentBond) {
        this.percentBond = percentBond;
    }

    public BigDecimal getStaticBond() {
        return staticBond;
    }

    public void setStaticBond(BigDecimal staticBond) {
        this.staticBond = staticBond;
    }

    public String getBaseBond() {
        return baseBond;
    }

    public void setBaseBond(String baseBond) {
        this.baseBond = baseBond;
    }

    public BigDecimal getPercentLateFee() {
        return percentLateFee;
    }

    public void setPercentLateFee(BigDecimal percentLateFee) {
        this.percentLateFee = percentLateFee;
    }

    public BigDecimal getStaticLateFee() {
        return staticLateFee;
    }

    public void setStaticLateFee(BigDecimal staticLateFee) {
        this.staticLateFee = staticLateFee;
    }

    public BigDecimal getMinLateFee() {
        return minLateFee;
    }

    public void setMinLateFee(BigDecimal minLateFee) {
        this.minLateFee = minLateFee;
    }

    public String getBaseLateFee() {
        return baseLateFee;
    }

    public void setBaseLateFee(String baseLateFee) {
        this.baseLateFee = baseLateFee;
    }

    public BigDecimal getPercentPenalties() {
        return percentPenalties;
    }

    public void setPercentPenalties(BigDecimal percentPenalties) {
        this.percentPenalties = percentPenalties;
    }

    public BigDecimal getStaticPenalties() {
        return staticPenalties;
    }

    public void setStaticPenalties(BigDecimal staticPenalties) {
        this.staticPenalties = staticPenalties;
    }

    public BigDecimal getMinPenalties() {
        return minPenalties;
    }

    public void setMinPenalties(BigDecimal minPenalties) {
        this.minPenalties = minPenalties;
    }

    public String getBasePenalties() {
        return basePenalties;
    }

    public void setBasePenalties(String basePenalties) {
        this.basePenalties = basePenalties;
    }

    public BigDecimal getPercentAllRepay() {
        return percentAllRepay;
    }

    public void setPercentAllRepay(BigDecimal percentAllRepay) {
        this.percentAllRepay = percentAllRepay;
    }

    public BigDecimal getStaticAllRepay() {
        return staticAllRepay;
    }

    public void setStaticAllRepay(BigDecimal staticAllRepay) {
        this.staticAllRepay = staticAllRepay;
    }

    public BigDecimal getMinAllRepay() {
        return minAllRepay;
    }

    public void setMinAllRepay(BigDecimal minAllRepay) {
        this.minAllRepay = minAllRepay;
    }

    public String getBaseAllRepay() {
        return baseAllRepay;
    }

    public void setBaseAllRepay(String baseAllRepay) {
        this.baseAllRepay = baseAllRepay;
    }

    public BigDecimal getPercentRefund() {
        return percentRefund;
    }

    public void setPercentRefund(BigDecimal percentRefund) {
        this.percentRefund = percentRefund;
    }

    public BigDecimal getStaticRefund() {
        return staticRefund;
    }

    public void setStaticRefund(BigDecimal staticRefund) {
        this.staticRefund = staticRefund;
    }

    public BigDecimal getMinRefund() {
        return minRefund;
    }

    public void setMinRefund(BigDecimal minRefund) {
        this.minRefund = minRefund;
    }

    public String getBaseRefund() {
        return baseRefund;
    }

    public void setBaseRefund(String baseRefund) {
        this.baseRefund = baseRefund;
    }

    public int getGraceLate() {
        return graceLate;
    }

    public void setGraceLate(int graceLate) {
        this.graceLate = graceLate;
    }

    public int getGracePenalties() {
        return gracePenalties;
    }

    public void setGracePenalties(int gracePenalties) {
        this.gracePenalties = gracePenalties;
    }

    public String getPayDateType() {
        return payDateType;
    }

    public void setPayDateType(String payDateType) {
        this.payDateType = payDateType;
    }

    public int getPayDate() {
        return payDate;
    }

    public void setPayDate(int payDate) {
        this.payDate = payDate;
    }

    public String getRefundDay() {
        return refundDay;
    }

    public void setRefundDay(String refundDay) {
        this.refundDay = refundDay;
    }

    public String getCanRepayAll() {
        return canRepayAll;
    }

    public void setCanRepayAll(String canRepayAll) {
        this.canRepayAll = canRepayAll;
    }

    public int getTermDays() {
        return termDays;
    }

    public void setTermDays(int termDays) {
        this.termDays = termDays;
    }

    public int getTermTenor() {
        return termTenor;
    }

    public void setTermTenor(int termTenor) {
        this.termTenor = termTenor;
    }

    public String getTermTime() {
        return termTime;
    }

    public void setTermTime(String termTime) {
        this.termTime = termTime;
    }

    public BigDecimal getFirstPayAmount() {
        return firstPayAmount;
    }

    public void setFirstPayAmount(BigDecimal firstPayAmount) {
        this.firstPayAmount = firstPayAmount;
    }

    public String getFirstPayTime() {
        return firstPayTime;
    }

    public void setFirstPayTime(String firstPayTime) {
        this.firstPayTime = firstPayTime;
    }

    public BigDecimal getPercentFirstPay() {
        return percentFirstPay;
    }

    public void setPercentFirstPay(BigDecimal percentFirstPay) {
        this.percentFirstPay = percentFirstPay;
    }

    public BigDecimal getStaticFirstPay() {
        return staticFirstPay;
    }

    public void setStaticFirstPay(BigDecimal staticFirstPay) {
        this.staticFirstPay = staticFirstPay;
    }

    public BigDecimal getAllRemainCorpus() {
        return allRemainCorpus;
    }

    public void setAllRemainCorpus(BigDecimal allRemainCorpus) {
        this.allRemainCorpus = allRemainCorpus;
    }

    public BigDecimal getAllRemainFee() {
        return allRemainFee;
    }

    public void setAllRemainFee(BigDecimal allRemainFee) {
        this.allRemainFee = allRemainFee;
    }

    public String getNextPayDate() {
        return nextPayDate;
    }

    public void setNextPayDate(String nextPayDate) {
        this.nextPayDate = nextPayDate;
    }

    public String getPrevPayDate() {
        return prevPayDate;
    }

    public void setPrevPayDate(String prevPayDate) {
        this.prevPayDate = prevPayDate;
    }

    public BigDecimal getPrevPayAmount() {
        return prevPayAmount;
    }

    public void setPrevPayAmount(BigDecimal prevPayAmount) {
        this.prevPayAmount = prevPayAmount;
    }

    public BigDecimal getNextPayAmount() {
        return nextPayAmount;
    }

    public void setNextPayAmount(BigDecimal nextPayAmount) {
        this.nextPayAmount = nextPayAmount;
    }

    public BigDecimal getNextPayFee() {
        return nextPayFee;
    }

    public void setNextPayFee(BigDecimal nextPayFee) {
        this.nextPayFee = nextPayFee;
    }

    public BigDecimal getNextPayServiceCharge() {
        return nextPayServiceCharge;
    }

    public void setNextPayServiceCharge(BigDecimal nextPayServiceCharge) {
        this.nextPayServiceCharge = nextPayServiceCharge;
    }

    public BigDecimal getAdjAmount() {
        return adjAmount;
    }

    public void setAdjAmount(BigDecimal adjAmount) {
        this.adjAmount = adjAmount;
    }

    public BigDecimal getAdjCorpus() {
        return adjCorpus;
    }

    public void setAdjCorpus(BigDecimal adjCorpus) {
        this.adjCorpus = adjCorpus;
    }

    public BigDecimal getAdjFee() {
        return adjFee;
    }

    public void setAdjFee(BigDecimal adjFee) {
        this.adjFee = adjFee;
    }

    public BigDecimal getAdjLateFee() {
        return adjLateFee;
    }

    public void setAdjLateFee(BigDecimal adjLateFee) {
        this.adjLateFee = adjLateFee;
    }

    public BigDecimal getAdjPenalties() {
        return adjPenalties;
    }

    public void setAdjPenalties(BigDecimal adjPenalties) {
        this.adjPenalties = adjPenalties;
    }

    public BigDecimal getAdjOtherFee() {
        return adjOtherFee;
    }

    public void setAdjOtherFee(BigDecimal adjOtherFee) {
        this.adjOtherFee = adjOtherFee;
    }

    public BigDecimal getCanBackCorpus() {
        return canBackCorpus;
    }

    public void setCanBackCorpus(BigDecimal canBackCorpus) {
        this.canBackCorpus = canBackCorpus;
    }

    public BigDecimal getCanBackFee() {
        return canBackFee;
    }

    public void setCanBackFee(BigDecimal canBackFee) {
        this.canBackFee = canBackFee;
    }

    public int getAheadOfPayDays() {
        return aheadOfPayDays;
    }

    public void setAheadOfPayDays(int aheadOfPayDays) {
        this.aheadOfPayDays = aheadOfPayDays;
    }

    public BigDecimal getPartRefundAmount() {
        return partRefundAmount;
    }

    public void setPartRefundAmount(BigDecimal partRefundAmount) {
        this.partRefundAmount = partRefundAmount;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public BigDecimal getRemainCorpus() {
        return remainCorpus;
    }

    public void setRemainCorpus(BigDecimal remainCorpus) {
        this.remainCorpus = remainCorpus;
    }

    public BigDecimal getRemainFee() {
        return remainFee;
    }

    public void setRemainFee(BigDecimal remainFee) {
        this.remainFee = remainFee;
    }

    public BigDecimal getRemainLateFee() {
        return remainLateFee;
    }

    public void setRemainLateFee(BigDecimal remainLateFee) {
        this.remainLateFee = remainLateFee;
    }

    public BigDecimal getRemainPenalties() {
        return remainPenalties;
    }

    public void setRemainPenalties(BigDecimal remainPenalties) {
        this.remainPenalties = remainPenalties;
    }

    public BigDecimal getRemainOtherFee() {
        return remainOtherFee;
    }

    public void setRemainOtherFee(BigDecimal remainOtherFee) {
        this.remainOtherFee = remainOtherFee;
    }

    public BigDecimal getRemainServiceCharge() {
        return remainServiceCharge;
    }

    public void setRemainServiceCharge(BigDecimal remainServiceCharge) {
        this.remainServiceCharge = remainServiceCharge;
    }

    public BigDecimal getPercentMerchantRefund() {
        return percentMerchantRefund;
    }

    public void setPercentMerchantRefund(BigDecimal percentMerchantRefund) {
        this.percentMerchantRefund = percentMerchantRefund;
    }

    public int getRemainPayTenor() {
        return remainPayTenor;
    }

    public void setRemainPayTenor(int remainPayTenor) {
        this.remainPayTenor = remainPayTenor;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public BigDecimal getDdgPercentFeep() {
        return ddgPercentFeep;
    }

    public void setDdgPercentFeep(BigDecimal ddgPercentFeep) {
        this.ddgPercentFeep = ddgPercentFeep;
    }

    public BigDecimal getDdgPercentRiskFee() {
        return ddgPercentRiskFee;
    }

    public void setDdgPercentRiskFee(BigDecimal ddgPercentRiskFee) {
        this.ddgPercentRiskFee = ddgPercentRiskFee;
    }

    public int getPercentMerchantFlag() {
        return percentMerchantFlag;
    }

    public void setPercentMerchantFlag(int percentMerchantFlag) {
        this.percentMerchantFlag = percentMerchantFlag;
    }

    public BigDecimal getOriginUserPercent() {
        return originUserPercent;
    }

    public void setOriginUserPercent(BigDecimal originUserPercent) {
        this.originUserPercent = originUserPercent;
    }

    public BigDecimal getOriginMerchantPercent() {
        return originMerchantPercent;
    }

    public void setOriginMerchantPercent(BigDecimal originMerchantPercent) {
        this.originMerchantPercent = originMerchantPercent;
    }

    public String getFundId() {
        return fundId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public BigDecimal getOriginLoanAmount() {
        return originLoanAmount;
    }

    public void setOriginLoanAmount(BigDecimal originLoanAmount) {
        this.originLoanAmount = originLoanAmount;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAssureFlag() {
        return assureFlag;
    }

    public void setAssureFlag(String assureFlag) {
        this.assureFlag = assureFlag;
    }

    public int getDebtFlag() {
        return debtFlag;
    }

    public void setDebtFlag(int debtFlag) {
        this.debtFlag = debtFlag;
    }

    public int getRepayDelay() {
        return repayDelay;
    }

    public void setRepayDelay(int repayDelay) {
        this.repayDelay = repayDelay;
    }

    public BigDecimal getAssureAmount() {
        return assureAmount;
    }

    public void setAssureAmount(BigDecimal assureAmount) {
        this.assureAmount = assureAmount;
    }

    public String getContractDueDate() {
        return contractDueDate;
    }

    public void setContractDueDate(String contractDueDate) {
        this.contractDueDate = contractDueDate;
    }

    public String getFirstPayDate() {
        return firstPayDate;
    }

    public void setFirstPayDate(String firstPayDate) {
        this.firstPayDate = firstPayDate;
    }

    public String getFinanceProductCode() {
        return financeProductCode;
    }

    public void setFinanceProductCode(String financeProductCode) {
        this.financeProductCode = financeProductCode;
    }

    public int getMerchantPushFlag() {
        return merchantPushFlag;
    }

    public void setMerchantPushFlag(int merchantPushFlag) {
        this.merchantPushFlag = merchantPushFlag;
    }

    public String getIndustryCate() {
        return industryCate;
    }

    public void setIndustryCate(String industryCate) {
        this.industryCate = industryCate;
    }

    public String getIndustryCateCode() {
        return industryCateCode;
    }

    public void setIndustryCateCode(String industryCateCode) {
        this.industryCateCode = industryCateCode;
    }

    public Boolean getEducationFree() {
        return educationFree;
    }

    public void setEducationFree(Boolean educationFree) {
        this.educationFree = educationFree;
    }

    public String getXjdChannelName() {
        return xjdChannelName;
    }

    public void setXjdChannelName(String xjdChannelName) {
        this.xjdChannelName = xjdChannelName;
    }

    public BigDecimal getBorrowerBondPercent() {
        return borrowerBondPercent;
    }

    public void setBorrowerBondPercent(BigDecimal borrowerBondPercent) {
        this.borrowerBondPercent = borrowerBondPercent;
    }

    public BigDecimal getSupplierFeePercent() {
        return supplierFeePercent;
    }

    public void setSupplierFeePercent(BigDecimal supplierFeePercent) {
        this.supplierFeePercent = supplierFeePercent;
    }

    public BigDecimal getBorrowerBondAmount() {
        return borrowerBondAmount;
    }

    public void setBorrowerBondAmount(BigDecimal borrowerBondAmount) {
        this.borrowerBondAmount = borrowerBondAmount;
    }

    public BigDecimal getSupplierFeeAmount() {
        return supplierFeeAmount;
    }

    public void setSupplierFeeAmount(BigDecimal supplierFeeAmount) {
        this.supplierFeeAmount = supplierFeeAmount;
    }

    public String getCanRenewalDate() {
        return canRenewalDate;
    }

    public void setCanRenewalDate(String canRenewalDate) {
        this.canRenewalDate = canRenewalDate;
    }

    public int getLoanFreezeFlag() {
        return loanFreezeFlag;
    }

    public void setLoanFreezeFlag(int loanFreezeFlag) {
        this.loanFreezeFlag = loanFreezeFlag;
    }

    public int getCavFlag() {
        return cavFlag;
    }

    public void setCavFlag(int cavFlag) {
        this.cavFlag = cavFlag;
    }

    public String getCavDate() {
        return cavDate;
    }

    public void setCavDate(String cavDate) {
        this.cavDate = cavDate;
    }

    public BigDecimal getBorrowAvailableBondAmount() {
        return borrowAvailableBondAmount;
    }

    public void setBorrowAvailableBondAmount(BigDecimal borrowAvailableBondAmount) {
        this.borrowAvailableBondAmount = borrowAvailableBondAmount;
    }

    public int getRepayDateAfter() {
        return repayDateAfter;
    }

    public void setRepayDateAfter(int repayDateAfter) {
        this.repayDateAfter = repayDateAfter;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getOrgExtId() {
        return orgExtId;
    }

    public void setOrgExtId(String orgExtId) {
        this.orgExtId = orgExtId;
    }

    @Override
    public String toString() {
        return "LoanStatusVo{" +
                "contractId=" + contractId +
                ", contractNo='" + contractNo + '\'' +
                ", extId='" + extId + '\'' +
                ", contractExtId='" + contractExtId + '\'' +
                ", loanNo='" + loanNo + '\'' +
                ", loanStatus='" + loanStatus + '\'' +
                ", userId=" + userId +
                ", idNo='" + idNo + '\'' +
                ", userName='" + userName + '\'' +
                ", userAge=" + userAge +
                ", userAddress='" + userAddress + '\'' +
                ", homeCity='" + homeCity + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", companyName='" + companyName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", merchantCode='" + merchantCode + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", storeCode='" + storeCode + '\'' +
                ", storeName='" + storeName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", loanDate='" + loanDate + '\'' +
                ", loanAmount=" + loanAmount +
                ", merchantAmount=" + merchantAmount +
                ", merchantFee=" + merchantFee +
                ", bondAmount=" + bondAmount +
                ", productId=" + productId +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", tenor=" + tenor +
                ", repayType='" + repayType + '\'' +
                ", currTenor=" + currTenor +
                ", remainTenor=" + remainTenor +
                ", isOverdue='" + isOverdue + '\'' +
                ", overdueDays=" + overdueDays +
                ", overdueCode='" + overdueCode + '\'' +
                ", orderOverdueSituation='" + orderOverdueSituation + '\'' +
                ", currPayAmount=" + currPayAmount +
                ", currPayCorpus=" + currPayCorpus +
                ", currPayFee=" + currPayFee +
                ", currPayLateFee=" + currPayLateFee +
                ", currPayPenalties=" + currPayPenalties +
                ", currPayOtherFee=" + currPayOtherFee +
                ", currPayServiceCharge=" + currPayServiceCharge +
                ", currPaidAmount=" + currPaidAmount +
                ", currPaidCorpus=" + currPaidCorpus +
                ", currPaidFee=" + currPaidFee +
                ", currPaidLateFee=" + currPaidLateFee +
                ", currPaidPenalties=" + currPaidPenalties +
                ", currPaidOtherFee=" + currPaidOtherFee +
                ", currPaidServiceCharge=" + currPaidServiceCharge +
                ", allPayAmount=" + allPayAmount +
                ", allPayCorpus=" + allPayCorpus +
                ", allPayFee=" + allPayFee +
                ", allPayLateFee=" + allPayLateFee +
                ", allPayPenalties=" + allPayPenalties +
                ", allPayOtherFee=" + allPayOtherFee +
                ", allPayServiceCharge=" + allPayServiceCharge +
                ", allPaidAmount=" + allPaidAmount +
                ", allPaidCorpus=" + allPaidCorpus +
                ", allPaidFee=" + allPaidFee +
                ", allPaidLateFee=" + allPaidLateFee +
                ", allPaidPenalties=" + allPaidPenalties +
                ", allPaidOtherFee=" + allPaidOtherFee +
                ", allPaidServiceCharge=" + allPaidServiceCharge +
                ", remitAmount=" + remitAmount +
                ", remitCount=" + remitCount +
                ", percentMerchant=" + percentMerchant +
                ", staticMerchant=" + staticMerchant +
                ", baseMerchant=" + baseMerchant +
                ", percentUser=" + percentUser +
                ", staticUser=" + staticUser +
                ", baseUser='" + baseUser + '\'' +
                ", percentBond=" + percentBond +
                ", staticBond=" + staticBond +
                ", baseBond='" + baseBond + '\'' +
                ", percentLateFee=" + percentLateFee +
                ", staticLateFee=" + staticLateFee +
                ", minLateFee=" + minLateFee +
                ", baseLateFee='" + baseLateFee + '\'' +
                ", percentPenalties=" + percentPenalties +
                ", staticPenalties=" + staticPenalties +
                ", minPenalties=" + minPenalties +
                ", basePenalties='" + basePenalties + '\'' +
                ", percentAllRepay=" + percentAllRepay +
                ", staticAllRepay=" + staticAllRepay +
                ", minAllRepay=" + minAllRepay +
                ", baseAllRepay='" + baseAllRepay + '\'' +
                ", percentRefund=" + percentRefund +
                ", staticRefund=" + staticRefund +
                ", minRefund=" + minRefund +
                ", baseRefund='" + baseRefund + '\'' +
                ", graceLate=" + graceLate +
                ", gracePenalties=" + gracePenalties +
                ", payDateType='" + payDateType + '\'' +
                ", payDate=" + payDate +
                ", refundDay='" + refundDay + '\'' +
                ", canRepayAll='" + canRepayAll + '\'' +
                ", termDays=" + termDays +
                ", termTenor=" + termTenor +
                ", termTime='" + termTime + '\'' +
                ", firstPayAmount=" + firstPayAmount +
                ", firstPayTime='" + firstPayTime + '\'' +
                ", percentFirstPay=" + percentFirstPay +
                ", staticFirstPay=" + staticFirstPay +
                ", allRemainCorpus=" + allRemainCorpus +
                ", allRemainFee=" + allRemainFee +
                ", nextPayDate='" + nextPayDate + '\'' +
                ", prevPayDate='" + prevPayDate + '\'' +
                ", prevPayAmount=" + prevPayAmount +
                ", nextPayAmount=" + nextPayAmount +
                ", nextPayFee=" + nextPayFee +
                ", nextPayServiceCharge=" + nextPayServiceCharge +
                ", adjAmount=" + adjAmount +
                ", adjCorpus=" + adjCorpus +
                ", adjFee=" + adjFee +
                ", adjLateFee=" + adjLateFee +
                ", adjPenalties=" + adjPenalties +
                ", adjOtherFee=" + adjOtherFee +
                ", canBackCorpus=" + canBackCorpus +
                ", canBackFee=" + canBackFee +
                ", aheadOfPayDays=" + aheadOfPayDays +
                ", partRefundAmount=" + partRefundAmount +
                ", remainAmount=" + remainAmount +
                ", remainCorpus=" + remainCorpus +
                ", remainFee=" + remainFee +
                ", remainLateFee=" + remainLateFee +
                ", remainPenalties=" + remainPenalties +
                ", remainOtherFee=" + remainOtherFee +
                ", remainServiceCharge=" + remainServiceCharge +
                ", percentMerchantRefund=" + percentMerchantRefund +
                ", remainPayTenor=" + remainPayTenor +
                ", productType='" + productType + '\'' +
                ", ddgPercentFeep=" + ddgPercentFeep +
                ", ddgPercentRiskFee=" + ddgPercentRiskFee +
                ", percentMerchantFlag=" + percentMerchantFlag +
                ", originUserPercent=" + originUserPercent +
                ", originMerchantPercent=" + originMerchantPercent +
                ", fundId='" + fundId + '\'' +
                ", originLoanAmount=" + originLoanAmount +
                ", bankCard='" + bankCard + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", assureFlag='" + assureFlag + '\'' +
                ", debtFlag=" + debtFlag +
                ", repayDelay=" + repayDelay +
                ", assureAmount=" + assureAmount +
                ", contractDueDate='" + contractDueDate + '\'' +
                ", firstPayDate='" + firstPayDate + '\'' +
                ", financeProductCode='" + financeProductCode + '\'' +
                ", merchantPushFlag=" + merchantPushFlag +
                ", industryCate='" + industryCate + '\'' +
                ", industryCateCode='" + industryCateCode + '\'' +
                ", educationFree=" + educationFree +
                ", xjdChannelName='" + xjdChannelName + '\'' +
                ", borrowerBondPercent=" + borrowerBondPercent +
                ", supplierFeePercent=" + supplierFeePercent +
                ", borrowerBondAmount=" + borrowerBondAmount +
                ", supplierFeeAmount=" + supplierFeeAmount +
                ", canRenewalDate='" + canRenewalDate + '\'' +
                ", loanFreezeFlag=" + loanFreezeFlag +
                ", cavFlag=" + cavFlag +
                ", cavDate='" + cavDate + '\'' +
                ", borrowAvailableBondAmount=" + borrowAvailableBondAmount +
                ", repayDateAfter=" + repayDateAfter +
                ", businessType='" + businessType + '\'' +
                ", orgExtId='" + orgExtId + '\'' +
                '}';
    }
}
