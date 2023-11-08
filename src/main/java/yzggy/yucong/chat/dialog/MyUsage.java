package yzggy.yucong.chat.dialog;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyUsage implements Serializable {

    private long promptTokens;
    private long completionTokens;
    private long totalTokens;
}
