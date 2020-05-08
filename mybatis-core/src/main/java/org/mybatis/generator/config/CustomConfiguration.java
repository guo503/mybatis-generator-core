package org.mybatis.generator.config;

import org.mybatis.generator.internal.util.StringUtility;

/**
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

    public static boolean isEnable(CustomConfiguration customConfiguration) {
        return StringUtility.isTrue(customConfiguration.getEnable());
    }

    public static String getName(CustomConfiguration customConfiguration) {
        return customConfiguration.getName();
    }

    public static String getValue(CustomConfiguration customConfiguration) {
        return customConfiguration.getValue();
    }
}
