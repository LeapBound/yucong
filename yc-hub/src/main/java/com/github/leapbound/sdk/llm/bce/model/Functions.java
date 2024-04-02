package com.github.leapbound.sdk.llm.bce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Functions implements Serializable {

    private String name;
    private String description;
    private Parameters parameters;

}
