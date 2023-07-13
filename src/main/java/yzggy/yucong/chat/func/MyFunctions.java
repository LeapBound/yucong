package yzggy.yucong.chat.func;

import lombok.Data;

@Data
public class MyFunctions {
    /**
     * 方法名称
     */
    private String name;
    /**
     * 方法描述
     */
    private String description;
    /**
     * 方法参数
     * 扩展参数可以继承Parameters自己实现，json格式的数据
     */
    private MyParameters parameters;
}
