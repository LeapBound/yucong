package yzggy.yucong.chat.dialog;

import lombok.Data;

@Data
public class MyUsage {

    private long promptTokens;
    private long completionTokens;
    private long totalTokens;
}
