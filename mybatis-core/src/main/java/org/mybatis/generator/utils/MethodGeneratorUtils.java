package org.mybatis.generator.utils;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * Description:
 * Author: guos
 * Date: 2019/2/1 12:58
 **/
public class MethodGeneratorUtils {


    public static String getFullPoName(Context context, IntrospectedTable introspectedTable) {
        //设置参数类型
        String poPack = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String poName = introspectedTable.getDomainObjectName();
        return getFullClass(poPack, poName);
    }


    public static FullyQualifiedJavaType getPoType(Context context, IntrospectedTable introspectedTable) {
        return new FullyQualifiedJavaType(getFullPoName(context, introspectedTable));
    }


    public static String getCondName(String poName) {
        if (!StringUtility.stringHasValue(poName)) {
            return null;
        }
        return poName + CommonConstant.COND_SUFFIX;
    }


    public static String getVoName(PluginConfiguration extentModelPlugin, IntrospectedTable introspectedTable) {
        //设置参数类型
        String voSuffix = extentModelPlugin.getProperty("voSuffix");
        String condName = introspectedTable.getDomainObjectName() + voSuffix;
        if (!StringUtility.stringHasValue(condName)) {
            condName = CommonConstant.VO_SUFFIX;
        }
        return condName;
    }

    public static String getFullVoName(PluginConfiguration extentModelPlugin, IntrospectedTable introspectedTable) {
        //设置参数类型
        String voPack = extentModelPlugin.getProperty("voPack");
        String voSuffix = extentModelPlugin.getProperty("voSuffix");
        String voName = introspectedTable.getDomainObjectName() + voSuffix;
        return getFullClass(voPack, voName, voSuffix);
    }

    public static FullyQualifiedJavaType getVoType(PluginConfiguration extentModelPlugin, IntrospectedTable introspectedTable) {
        //设置参数类型
        String condPack = extentModelPlugin.getProperty("voPack");
        return new FullyQualifiedJavaType(condPack + "." + getVoName(extentModelPlugin, introspectedTable));
    }

    public static String getVoTypeStr(PluginConfiguration extentModelPlugin, IntrospectedTable introspectedTable) {
        //设置参数类型
        String voPack = extentModelPlugin.getProperty("voPack");
        return getFullClass(voPack, getVoName(extentModelPlugin, introspectedTable));
    }

    private static String getFullClass(String pack, String pojo, String suffix) {
        if (!StringUtility.stringHasValue(pack)) {
            throw new RuntimeException("包路径不能为空");
        }
        if (!StringUtility.stringHasValue(suffix)) {
            throw new RuntimeException("后缀名不能为空");
        }
        return pack + "." + pojo + MethodUtils.toUpperCase(suffix);
    }

    private static String getFullClass(String pack, String name) {
        if (!StringUtility.stringHasValue(pack)) {
            throw new RuntimeException("包路径不能为空");
        }
        if (!StringUtility.stringHasValue(name)) {
            throw new RuntimeException("类名不能为空");
        }
        return pack + "." + name;
    }
}
