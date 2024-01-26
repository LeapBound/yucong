package yucong.camunda.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yamath
 * @since 2023/11/17 11:12
 */
@Data
@NoArgsConstructor
public class ProcessStepInputForm {

    private String id;

    private String label;

    private String type;

    private String defaultValue;
}
