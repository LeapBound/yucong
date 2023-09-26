package yzggy.yucong.bce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponse implements Serializable {

    private String id;
    private String object;
    private long created;
    private String result;
    @JsonProperty("is_truncated")
    private Boolean isTruncated;
    @JsonProperty("need_clear_history")
    private Boolean needClearHistory;
    @JsonProperty("function_call")
    private FunctionCall functionCall;
    private Usage usage;
}
