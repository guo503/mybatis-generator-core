package org.mybatis.generator.config;

/**
 * method元素标签
 * @author guos
 * @description
 * @date 2020/4/27 18:18
 **/
public class MethodConfiguration {


    private String name;

    private String mapping;

    private String isDefault;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
