package geex.architecture.guts.camunda.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/11/22 17:57
 */
@Data
@NoArgsConstructor
public class ProcessVariablesRequest {

    private String processInstanceId;

    private String key;

    private String businessKey;

    private Map<String, Object> inputVariables;
}
