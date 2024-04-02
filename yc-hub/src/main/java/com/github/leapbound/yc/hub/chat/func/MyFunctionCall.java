package com.github.leapbound.yc.hub.chat.func;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyFunctionCall {

    private String name;
    private String arguments;

}
