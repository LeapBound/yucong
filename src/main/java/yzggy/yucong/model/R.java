package yzggy.yucong.model;

import lombok.Data;

@Data
public class R<T> {

    private int status;
    private T data;

    public static <D> R<D> fail() {
        R<D> r = new R<>();
        r.setStatus(400);
        return r;
    }

    public static <D> R<D> ok(D data) {
        R<D> r = new R<>();
        r.setStatus(200);
        r.setData(data);
        return r;
    }
}
