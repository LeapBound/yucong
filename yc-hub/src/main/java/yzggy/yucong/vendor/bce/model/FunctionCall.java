package yzggy.yucong.vendor.bce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FunctionCall {
    private String name;
    private String thoughts;
    private String arguments;
}
