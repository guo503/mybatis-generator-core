package org.mybatis.generator.utils;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

/**
 * 类工具
 * author guos
 * date: 2019/2/28 17:56
 **/
public class ClassUtils {

    public static void addComment(JavaElement field, String comment) {
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        comment = comment.replaceAll("\n", "<br>\n\t * ");
        sb.append(comment);
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
    }

    /**
     * add field
     *
     * @param topLevelClass
     */
    protected void addField(TopLevelClass topLevelClass) {
        // add success
        Field field = new Field();
        field.setName("success"); // set var name
        field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance()); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        addComment(field, "excute result");
        topLevelClass.addField(field);
        // set result
        field = new Field();
        field.setName("message"); // set result
        field.setType(FullyQualifiedJavaType.getStringInstance()); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        addComment(field, "message result");
        topLevelClass.addField(field);
    }


    /**
     * add field
     *
     * @param topLevelClass
     */
    public static void addEmptyField(TopLevelClass topLevelClass) {
        // add success
        Field field = new Field();
        field.setName(""); // set var name
        topLevelClass.addField(field);
    }


    /**
     * add field
     *
     * @param topLevelClass
     */
    public static void addStaticField(TopLevelClass topLevelClass, Object col, boolean hasBlank) {
        Field field = new Field();
        if (hasBlank) {
            field.addJavaDocLine("");
            field.addJavaDocLine("");
            field.addJavaDocLine("");
        }
        field.setName(col.toString().toUpperCase() + " = \"" + MethodUtils.lineToHump(col.toString()) + "\""); // set var name
        field.setVisibility(JavaVisibility.PUBLIC);
        field.setFinal(true);
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("String"));
        topLevelClass.addField(field);
    }


    /**
     * add method
     */
    protected void addMethod(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setSuccess");
        method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "success"));
        method.addBodyLine("this.success = success;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        method.setName("isSuccess");
        method.addBodyLine("return success;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setMessage");
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "message"));
        method.addBodyLine("this.message = message;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("getMessage");
        method.addBodyLine("return message;");
        topLevelClass.addMethod(method);
    }

    /**
     * add method
     */
    protected void addMethod(TopLevelClass topLevelClass, List<Method> methods, String mapperName) {
        Method method2;
        for (int i = 0; i < methods.size(); i++) {
            Method method;
            method2 = methods.get(i);
            method = method2;
            method.removeAllBodyLines();
            method.removeAnnotation();
            StringBuilder sb = new StringBuilder();
            sb.append("return this.");
            sb.append(mapperName);
            sb.append(method.getName());
            sb.append("(");
            List<Parameter> list = method.getParameters();
            for (int j = 0; j < list.size(); j++) {
                sb.append(list.get(j).getName());
                sb.append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append(");");
            method.addBodyLine(sb.toString());
            topLevelClass.addMethod(method);
        }
        methods.clear();
    }

    /**
     * import logger
     */
    public static void addLogger(TopLevelClass topLevelClass) {
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("LoggerFactory.getLogger(" + topLevelClass.getType().getShortName() + ".class)"); // set value
        field.setName("logger"); // set var name
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("Logger")); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addJavaDocLine("");
        topLevelClass.addField(field);
    }

    /**
     * 添加Field
     * flag: 1,business 2,controller
     *
     * @param topLevelClass
     */
    public static void addField(TopLevelClass topLevelClass, FullyQualifiedJavaType javaType, String remoteResource) {
        // add dao
        Field field = new Field();
        FullyQualifiedJavaType autowiredAnnotation = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
        String autowiredName = "@Autowired";
        field.setName(MethodUtils.toLowerCase(javaType.getShortName())); // set var name
        topLevelClass.addImportedType(javaType);
        field.setType(javaType); // type
        if (StringUtility.stringHasValue(remoteResource)) {
            FullyQualifiedJavaType remoteAnnotation = new FullyQualifiedJavaType(remoteResource);
            topLevelClass.addImportedType(remoteAnnotation);
            field.addAnnotation("@" + MethodUtils.toUpperCase(remoteAnnotation.getShortName()));
        } else {
            topLevelClass.addImportedType(autowiredAnnotation);
            field.addAnnotation(autowiredName);
        }
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addJavaDocLine("");
        topLevelClass.addField(field);
    }


    /**
     * 域添加注释
     * param field
     * param desc:描述
     * author  guos
     * date 2019/3/22 10:31
     * return
     **/
    public static void addFieldComment(Field field, String desc) {
        field.addJavaDocLine("/**");
        field.addJavaDocLine("*" + desc);
        field.addJavaDocLine("*/");
    }
}
