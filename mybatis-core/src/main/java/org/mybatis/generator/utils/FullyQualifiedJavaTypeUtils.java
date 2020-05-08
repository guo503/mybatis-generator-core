package org.mybatis.generator.utils;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * Description: 导入类
 * Author: guos
 * Date: 2019/1/25 17:28
 **/
public class FullyQualifiedJavaTypeUtils {

    /**
     * param interfaces
     * param topLevelClass
     * param clz： 类路径
     * Description:  导入类
     * Author: guos
     * Date: 2019/1/25 17:29
     * Return:
     **/
    public static void importType(Interface interfaces, TopLevelClass topLevelClass, String clz) {
        FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(clz);
        if (interfaces != null) {
            interfaces.addImportedType(fullyQualifiedJavaType);
        }
        if (topLevelClass != null) {
            topLevelClass.addImportedType(fullyQualifiedJavaType);
        }
    }
}
