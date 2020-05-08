package org.mybatis.generator.utils;

/**
 * @author guos
 * description
 * date 2020/5/7 10:52
 **/
public class CustomKeyUtil {


    public static String getPropKey(String type, String name) {
        return type + "." + name;
    }
}
