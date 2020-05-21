package org.mybatis.generator.config;

/**
 * @author guos
 * @description
 * @date 2020/4/27 18:18
 **/
public class TableProp extends PropertyHolder{

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
