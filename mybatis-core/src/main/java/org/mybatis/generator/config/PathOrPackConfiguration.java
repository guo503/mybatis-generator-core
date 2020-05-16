package org.mybatis.generator.config;

/**
 * @author guos
 * @description
 * @date 2020/4/27 18:18
 **/
public class PathOrPackConfiguration {

    private String name;

    private String value;

    private String type;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
