package yzggy.yucong.action.model.vo.request;

/**
 * @author yamath
 * @since 2023/10/12 10:55
 */
public class FunctionGroovySaveRequest extends BaseRequest {

    private Integer id;

    private String functionName;

    private String groovyName;

    private String groovyUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getGroovyName() {
        return groovyName;
    }

    public void setGroovyName(String groovyName) {
        this.groovyName = groovyName;
    }

    public String getGroovyUrl() {
        return groovyUrl;
    }

    public void setGroovyUrl(String groovyUrl) {
        this.groovyUrl = groovyUrl;
    }

    @Override
    public String toString() {
        return "FunctionGroovySaveRequest{" +
                "id=" + id +
                ", functionName='" + functionName + '\'' +
                ", groovyName='" + groovyName + '\'' +
                ", groovyUrl='" + groovyUrl + '\'' +
                '}';
    }
}
