package geex.architecture.guts.camunda.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/11/17 11:31
 */
@Data
@NoArgsConstructor
public class TaskCompleteRequest {

    private String key;

    private String processInstanceId;

    private String businessKey;

    private String taskId;

    private String activityId;

    private Map<String, Object> taskInputVariables;
}
