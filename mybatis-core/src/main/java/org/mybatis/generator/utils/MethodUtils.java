package org.mybatis.generator.utils;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Author: guos
 * Date: 2019/1/28 10:28
 **/
public class MethodUtils {

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    private static Pattern humpPattern = Pattern.compile("[A-Z]");


    public static void main(String[] args) {
        System.out.println(getResultFullMethod("com.tsyj.sdk.response.Result:success($)|fail($)|success($,$)", ":", 2));
    }


    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 生成get方法名
     *
     * @param pojo
     * @param colName
     * @return
     */
    public static String generateGet(String pojo, String colName) {
        return toLowerCase(pojo) + ".get" + toUpperCase(lineToHump(colName)) + "()";
    }


    /**
     * 生成set方法名
     *
     * @param pojo：对象
     * @param colName ：列名
     * @param val:值
     * @return
     */
    public static String generateSet(String pojo, String colName, String val) {
        return toLowerCase(pojo) + ".set" + toUpperCase(lineToHump(colName)) + "(" + val + ")";
    }


    /**
     * 驼峰转下划线,效率比上面高
     */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * BaseUsers to baseUsers
     *
     * @param tableName
     * @return
     */
    public static String toLowerCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * BaseUsers to baseUsers
     *
     * @param tableName
     * @return
     */
    public static String toUpperCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }


    /**
     * 下划线转中划线
     *
     * @param tableName
     * @return
     */
    public static String humpToMiddleLine(String tableName) {
        return tableName.replaceAll("_", "-");
    }


    /**
     * 根据方法路径获取类全路径
     * param str
     * param split :分割符
     * return
     */
    public static String getFullClass(String str, String split) {
        if (!StringUtility.stringHasValue(split)) {
            return str;
        }
        String res;
        if (".".equals(split)) {
            int index = str.lastIndexOf('.');
            res = str.substring(0, index);
        } else {
            res = str.split(split)[0];
        }
        return res;
    }

    /**
     * 根据方法路径获取类名
     * param str
     * param split :分割符
     * return
     */
    public static String getShortClass(String str, String split) {
        if (!StringUtility.stringHasValue(split)) {
            return str;
        }
        String res = getFullClass(str, split);
        return res.substring(res.lastIndexOf('.') + 1);
    }


    /**
     * 根据方法路径获取类名
     * param str
     * param split :分割符
     * return
     */
    public static String getClassName(String str, String split) {
        if (!StringUtility.stringHasValue(split)) {
            return str;
        }
        return str.substring(str.lastIndexOf('.') + 1);
    }

    /**
     * 根据方法路径获取方法名
     * param str
     * param split :分割符
     * return
     */
    public static String getMethod(String str, String split) {
        if (!StringUtility.stringHasValue(split)) {
            return str;
        }
        return str.substring(str.lastIndexOf('.') + 1);
    }

    /**
     * 根据方法路径获取方法全名
     * param str
     * param split :分割符
     * return
     */
    public static String getFullMethod(String str, String split) {
        if (!StringUtility.stringHasValue(split)) {
            return str;
        }
        return getShortClass(str, split) + "." + getMethod(str, split);
    }

    /**
     * 根据方法路径获取方法名
     * param str
     * param split :分割符
     * param index :方法下表
     * return
     */
    public static String getResultMethod(String str, String split, int index) {
        if (!StringUtility.stringHasValue(str)) {
            return str;
        }
        String res = str.split(split)[1];
        String[] methods = res.split("\\|");
        return methods[index];
    }

    /**
     * 根据方法路径获取方法名
     * param str
     * param split :分割符
     * param methodSplit : 方法分割符
     * param index :方法下表
     * return
     */
    public static String getResultFullMethod(String str, String split, int index) {
        return getShortClass(str, split) + "." + getResultMethod(str, split, index);
    }

    public static void addLoggerInfo(Method method, String param) {
        method.addBodyLine("logger.info(\"-----" + method.getName() + "------,param: {}\"," + param + ");");
    }


    public static void printLoggerInfo(Method method, String desc) {
        method.addBodyLine("logger.info(\"" + desc + "\");");
    }


    public static void addLoggerInfo(Method method, String[] params) {
        StringBuilder sb = new StringBuilder();
        int len = params.length;
        for (int i = 0; i < len; i++) {
            if (i == len - 1) {
                sb.append("param" + i + ":" + "{}");
            } else {
                sb.append("param" + i + ":" + "{},");
            }
        }
        sb.append("\"");
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i == len - 1) {
                sb2.append(params[i]);
            } else {
                sb2.append("," + params[i] + ",");
            }
        }
        sb2.append(");");
        method.addBodyLine("logger.info(\"-----" + method.getName() + "------," + sb.toString() + sb2.toString());
    }

    /**
     * Description: 组装方法信息，返回字符串
     * param method
     * param type: 1,接口 2，实现类
     * Author: guos
     * Date: 2019/2/18 10:24
     * Return:
     **/
    public static String getMethodStr(Method method, int type) {
        List<String> javaDocLines = method.getJavaDocLines();
        String TabStr = "\t\t\t";
        StringBuilder sb = new StringBuilder();
        for (String str : javaDocLines) {
            sb.append(TabStr + str.trim() + "\n");
        }
        String methodStr = getMethodSign(method);
        if (type == 1) {
            sb.append(TabStr + methodStr + ";");
        } else {
            sb.append(TabStr + "@Override" + "\n");
            if (MethodEnum.SAVE_AND_GET.getValue().equals(method.getName())) {
                sb.append(TabStr + "@Transactional" + "\n");
            }
            sb.append(TabStr + "public " + methodStr + "{\n");
            List<String> bodyLines = method.getBodyLines();
            for (String str : bodyLines) {
                sb.append(TabStr + str + "\n");
            }
            sb.append(TabStr + "}");
        }
        return sb.toString();
    }


    /**
     * Description: 生成方法签名
     * param method
     * Author: guos
     * Date: 2019/2/18 16:22
     * Return:
     **/
    public static String getMethodSign(Method method) {
        String methodName = method.getName();
        List<Parameter> parameters = method.getParameters();
        StringBuilder resStr = new StringBuilder(method.getReturnType().getShortName() + " " + methodName + "(");
        for (int i = 0; i < parameters.size(); i++) {
            String type = parameters.get(i).getType().getShortName();
            String parameterType;
            if (type.contains(".")) {
                parameterType = MethodUtils.getMethod(type, ".");
            } else {
                parameterType = type;
            }
            if (i == parameters.size() - 1) {
                resStr.append(parameterType + " " + parameters.get(i).getName());
            } else {
                resStr.append(parameterType + " " + parameters.get(i).getName() + ", ");
            }
        }
        resStr.append(")");
        return resStr.toString();
    }


    /**
     * Description: 生成方法签名
     * param methodName 方法名
     * param parameterType 参数类型
     * param parameterName 参数
     * param returnType 返回类型
     * Author: guos
     * Date: 2019/2/18 16:22
     * Return:
     **/
    public static String getMethodSign(String methodName, String parameterType, String parameterName, String returnType) {
        return returnType + " " + methodName + "(" + parameterType + " " + parameterName + ")";
    }

    public static String getMethodStr(String methodName, String parameterType, String parameterName, String returnType, IntrospectedTable introspectedTable, Context context) {
        String methodSign = getMethodSign(methodName, parameterType, parameterName, returnType) + ";";
        String Cache = context.getProperty("Cache");
        String CacheDelete = context.getProperty("CacheDelete");
        String CacheDeleteKey = context.getProperty("CacheDeleteKey");
        if (StringUtility.stringHasValue(Cache) && StringUtility.stringHasValue(CacheDelete) && StringUtility.stringHasValue(CacheDeleteKey)) {
            String methodAnnotation = AnnotationUtils.addAnnotation(methodSign, methodName, introspectedTable.getDomainObjectName());
            return CommentUtils.addGeneralMethodComment(methodAnnotation, methodName, parameterName, returnType, introspectedTable);
        }
        return CommentUtils.addGeneralMethodComment(methodSign, methodName, parameterName, returnType, introspectedTable);
    }

    /**
     * 生成查询单个对象方法
     * param domainName
     * return
     */
    public static String getSelectPoMethodStr(String domainName) {
        return "get" + toUpperCase(domainName);
    }

    public static FullyQualifiedJavaType getResponseType(String responseMethod, String content) {
        String responseType = MethodUtils.getShortClass(responseMethod, ":") + "<" + content + ">";
        return new FullyQualifiedJavaType(responseType);
    }

    public static String getResponseMethod(String responseMethod, int index) {
        return MethodUtils.getResultFullMethod(responseMethod, ":", index);
    }


    /**
     * 根据类全路径返回类名
     * param pageStr
     * author  guos
     * date 2019/3/22 16:38
     * return
     **/
    public static String getClassName(String ClassName) {
        return ClassName.substring(ClassName.lastIndexOf('.') + 1);
    }

    public static void clear(Method method){
        if(Objects.isNull(method)){
            return;
        }
        method.removeAnnotation();
        method.removeAllBodyLines();
    }

    public static String getPrimaryKeyName(IntrospectedTable introspectedTable) {
        return introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();
    }

    public static String getPrimaryKeyType(IntrospectedTable introspectedTable) {
        return introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getShortName();
    }

    public static FullyQualifiedJavaType getPrimaryKeyFullyQualifiedJavaType(IntrospectedTable introspectedTable) {
        return introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
    }

    public static String getFullQueryName(String poName, String queryPack, String querySuffix) {
        return queryPack + "." + poName + querySuffix;
    }

    public static String getFullVoName(String poName, String voPack, String voSuffix) {
        return voPack + "." + poName + voSuffix;
    }

}
