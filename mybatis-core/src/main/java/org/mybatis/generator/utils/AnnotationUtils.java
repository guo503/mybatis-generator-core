package org.mybatis.generator.utils;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * Description:
 * Author: guos
 * Date: 2019/1/20 17:10
 **/
public class AnnotationUtils {


    public static void addAnnotation(Method method, IntrospectedTable introspectedTable, Interface inter) {
        Context context = introspectedTable.getContext();
        String Cache = context.getProperty("Cache");
        String CacheDelete = context.getProperty("CacheDelete");
        String CacheDeleteKey = context.getProperty("CacheDeleteKey");
        if (StringUtility.stringHasValue(Cache) && StringUtility.stringHasValue(CacheDelete) && StringUtility.stringHasValue(CacheDeleteKey)) {
            String methodName = method.getName();
            //对象名
            String domainName = MethodUtils.toLowerCase(introspectedTable.getDomainObjectName());
            setAnnotation(method, methodName, null, domainName, 1);

            FullyQualifiedJavaType CacheAnno = new FullyQualifiedJavaType(Cache);
            inter.addImportedType(CacheAnno);
            FullyQualifiedJavaType CacheDeleteAnno = new FullyQualifiedJavaType(CacheDelete);
            inter.addImportedType(CacheDeleteAnno);
            FullyQualifiedJavaType CacheDeleteKeyAnno = new FullyQualifiedJavaType(CacheDeleteKey);
            inter.addImportedType(CacheDeleteKeyAnno);
        }
    }


    public static String addAnnotation(String methodSign, String methodName, String domainName) {
        return setAnnotation(null, methodName, methodSign, MethodUtils.toLowerCase(domainName), 2);
    }


    /**
     * param method
     * param methodSign
     * param type:1，service 2,mapper
     * author guos
     * date 2019/2/27 9:34
     * return
     **/
    private static String setAnnotation(Method method, String methodName, String methodSign, String domainName, int type) {
        //对象复数
        String domainNames;
        if (domainName.endsWith("s")) {
            domainNames = domainName + "es";
        } else {
            domainNames = domainName + "s";
        }
        StringBuilder sb = new StringBuilder();
        String cacheStr = "";
        String tabStr = "\n";
        if (MethodEnum.GET.getValue().equals(methodName)) {
            cacheStr = "@Cache(key = \"" + domainName + "\")";
            if (type == 1) {
                method.addAnnotation(cacheStr);
            } else {
                sb.append(cacheStr + tabStr);
            }
        } else if (MethodUtils.getSelectPoMethodStr(domainName).equals(methodName)) {
            cacheStr = "@Cache(key = \"" + domainName + "\")";
            if (type == 1) {
                method.addAnnotation(cacheStr);
            } else {
                sb.append(cacheStr + tabStr);
            }
        } else if (MethodEnum.SAVE.getValue().equals(methodName)) {
            cacheStr = "@CacheDelete({@CacheDeleteKey(key = \"" + domainNames + "\",all = true)})";
            if (type == 1) {
                method.addAnnotation(cacheStr);
            } else {
                sb.append(cacheStr + tabStr);
            }
        } else if (MethodEnum.UPDATE.getValue().equals(methodName)) {
            cacheStr = "@CacheDelete({@CacheDeleteKey(key = \"" + domainNames + "\",all = true),@CacheDeleteKey(key =\"" + domainName + "\",field = \"get\", el =\"#" + domainName + ".id\")})";
            if (type == 1) {
                method.addAnnotation(cacheStr);
            } else {
                sb.append(cacheStr + tabStr);
            }
        } else if (MethodEnum.LIST.getValue().equals(methodName) || MethodEnum.LIST_BY_IDS.getValue().equals(methodName) || MethodEnum.COUNT.getValue().equals(methodName)) {
            cacheStr = cacheStr + "@Cache(key = \"" + domainName + "\")";
            if (type == 1) {
                method.addAnnotation("@Cache(key = \"" + domainNames + "\")");
            } else {
                sb.append(cacheStr + tabStr);
            }
        }
        sb.append(methodSign);
        return sb.toString();
    }


    /**
     * 根据表字段获取属性字母小写驼峰
     * param introspectedTable
     * Description: Author: guos
     * Date: 2019/1/25 11:59
     * Return:
     **/
    public static String getAttrNameByColumn(String column) {
        if (StringUtility.stringHasValue(column)) {
            String str = MethodUtils.lineToHump(column);
            if (StringUtility.stringHasValue(str)) {
                return str.substring(0, 1).toUpperCase() + str.substring(1);
            }
        }
        return null;
    }

    /**
     * 根据表字段获取属性字母小写驼峰
     * param base: 注解名
     * param extend: 注解属性
     * Description: Author: guos
     * Date: 2019/1/25 11:59
     * Return:
     **/
    public static String generateAnnotation(String base, String extend) {
        if (!StringUtility.stringHasValue(extend)) {
            return base;
        }
        return base + "(\"/" + extend + "\")";
    }
}
