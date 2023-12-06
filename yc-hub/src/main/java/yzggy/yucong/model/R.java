package yzggy.yucong.model;

import lombok.Data;

@Data
public class R<T> {

    private int status;
    private T data;
    private String errorMsg;

    public static <D> R<D> fail() {
        return fail("");
    }

    public static <D> R<D> fail(String errorMsg) {
        R<D> r = new R<>();
        r.setStatus(400);
        r.setErrorMsg(errorMsg);
        return r;
    }

    public static <D> R<D> ok() {
        return ok(null);
    }

    public static <D> R<D> ok(D data) {
        R<D> r = new R<>();
        r.setStatus(200);
        r.setData(data);
        return r;
    }
}
