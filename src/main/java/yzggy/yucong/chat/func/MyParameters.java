package yzggy.yucong.chat.func;

import lombok.Data;

import java.util.List;

@Data
public class MyParameters {
    /**
     * 参数类型
     */
    private String type;
    /**
     * 参数属性、描述
     */
    private Object properties;
    /**
     * 方法必输字段
     */
    private List<String> required;
}
