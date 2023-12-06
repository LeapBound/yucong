package yzggy.yucong.action.func.order.enums;

/**
 * @author yamath
 * @since 2023/8/10 15:21
 */
public enum LoanCertificateStatusEnum {

    取消(0),
    正常(1),
    退货预约(2),
    退货完成(3),
    提前还款预约(4),
    提前还款完成(5),
    逾期终止(6),
    手动终止(7),
    完成(8);

    private int status;

    LoanCertificateStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
