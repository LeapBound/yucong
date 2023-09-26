package yzggy.yucong.chat.dialog;

import lombok.Data;
import yzggy.yucong.chat.func.MyFunctionCall;

@Data
public class MyChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String result;
    private String model;
    private Boolean isTruncated;
    private Boolean needClearHistory;
    private MyMessage message;
    private MyFunctionCall functionCall;
    private MyUsage usage;
    private String warning;
}
