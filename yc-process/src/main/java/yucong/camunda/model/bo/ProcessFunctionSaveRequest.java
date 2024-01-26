package geex.architecture.guts.camunda.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yamath
 * @since 2023/11/24 11:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProcessFunctionSaveRequest extends BaseRequest {

    private String processKey;

    private String activityId;

    private String functionName;

    private Integer inuse;
}
