package org.mybatis.generator.method;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.utils.MethodUtils;

/**
 * Description:
 * Author: guos
 * Date: 2019/1/31 12:43
 **/
public class CommonGen {


    public static String getObjectWithSuffix(String base, String suffix) {
        if (StringUtility.stringHasValue(suffix)) {
            return base + suffix;
        }
        return base;
    }

    public static String getShortName(FullyQualifiedJavaType type) {
        return MethodUtils.toLowerCase(type.getShortName()) + ".";
    }


    public static void setMethodPrimaryKey(IntrospectedTable introspectedTable, Method method) {
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
    }


    public static void setMethodParameter(Method method, String domainName, String suffix) {
        String name = MethodUtils.toLowerCase(domainName) + suffix;
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(domainName + suffix);
        method.addParameter(new Parameter(type, name));
    }

    public static void setMethodParameter(Method method, String domainName) {
        String name = MethodUtils.toLowerCase(domainName);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(domainName);
        method.addParameter(new Parameter(type, name));
    }
}



