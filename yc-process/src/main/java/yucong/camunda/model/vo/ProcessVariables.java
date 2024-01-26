package yucong.camunda.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/11/22 17:59
 */
@Data
@NoArgsConstructor
public class ProcessVariables {

    private String processInstanceId;

    private Map<String, Object> processVariables;
}
