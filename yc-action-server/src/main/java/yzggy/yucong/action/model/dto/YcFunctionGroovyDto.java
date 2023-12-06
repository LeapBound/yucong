package yzggy.yucong.action.model.dto;

import java.io.Serializable;

/**
 * @author yamath
 * @since 2023/10/12 10:48
 */
public class YcFunctionGroovyDto implements Serializable {

    /**
     * 方法名
     */
    private String functionName;

    /**
     * groovy 脚本名
     */
    private String groovyName;

    /**
     * groovy 脚本地址
     */
    private String groovyUrl;

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
        return "YcFunctionGroovyDto{" +
                "functionName='" + functionName + '\'' +
                ", groovyName='" + groovyName + '\'' +
                ", groovyUrl='" + groovyUrl + '\'' +
                '}';
    }
}
