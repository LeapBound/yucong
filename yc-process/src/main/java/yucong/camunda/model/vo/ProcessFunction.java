package geex.architecture.guts.camunda.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yamath
 * @since 2023/11/24 11:31
 */
@Data
@NoArgsConstructor
public class ProcessFunction {

    private String processKey;

    private List<TaskFunction> taskFunctions;
}
