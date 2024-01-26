package geex.architecture.guts.camunda.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/11/17 11:25
 */
@Data
@NoArgsConstructor
public class ProcessStartRequest {

    private String key;

    private String processKey;

    private String businessKey;

    private Map<String, Object> startFormVariables;
}
