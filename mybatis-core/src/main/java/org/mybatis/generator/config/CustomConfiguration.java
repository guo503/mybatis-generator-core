package org.mybatis.generator.config;

/**
 * prop元素标签
 * @author guos
 * @description
 * @date 2020/4/27 18:18
 **/
public class CustomConfiguration {


    private String name;

    private String value;

    private String enable;


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

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

}
