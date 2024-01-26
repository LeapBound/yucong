package geex.architecture.guts.camunda.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yamath
 * @since 2023/11/24 11:41
 */
@Data
public class BaseRequest implements Serializable {

    private String username;

    private String accountId;

    private int rows;

    private int page;
}
