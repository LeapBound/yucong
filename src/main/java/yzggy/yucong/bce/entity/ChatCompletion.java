package yzggy.yucong.bce.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletion implements Serializable {

    private String system;
    private Boolean stream;

    @NonNull
    private List<Message> messages;

    private List<Functions> functions;

    /**
     * 取值：null,auto或者自定义
     * functions没有值的时候默认为：null
     * functions存在值得时候默认为：auto
     * 也可以自定义
     */
    @JsonProperty("function_call")
    private Object functionCall;

}
