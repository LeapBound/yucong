package com.github.leapbound.sdk.llm.bce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameters implements Serializable {

    private String type;
    private Object properties;
    private List<String> required;

}
