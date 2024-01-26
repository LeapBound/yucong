package yzggy.yucong.action.func.order.enums;

/**
 * @author yamath
 * @since 2023/8/10 13:32
 */
public enum LoanOrderStatusEnum {
    待放款("LOAN_WAIT", "待放款(等待放款申请)"),
    放款中("LOAN_ING", "放款中"),
    放款成功("LOAN_SUCC", "放款成功"),
    放款失败("LOAN_FAIL", "放款失败"),
    审批通过("AUDIT_PASS", "审批通过,待分配");

    private String orderStatus;

    private String statusDesc;

    LoanOrderStatusEnum(String orderStatus, String statusDesc) {
        this.orderStatus = orderStatus;
        this.statusDesc = statusDesc;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public static String getStatusDesc(String orderStatus) {
        for (LoanOrderStatusEnum item : LoanOrderStatusEnum.values()) {
            if (orderStatus.equals(item.orderStatus)) {
                return item.statusDesc;
            }
        }
        throw new IllegalArgumentException("orderStatus is not valid");
    }
}
