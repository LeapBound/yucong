package com.github.leapbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Fred Gu
 * @date 2024-12-06 13:38
 */
@Data
@AllArgsConstructor
public class FunctionParamModel {

    private String type;
    private Map<String, Object> properties;
    private List<String> required;

}
