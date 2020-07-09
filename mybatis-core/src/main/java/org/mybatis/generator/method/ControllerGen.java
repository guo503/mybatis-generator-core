package org.mybatis.generator.method;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.plugins.ExtendModelPlugin;
import org.mybatis.generator.utils.AnnotationUtils;
import org.mybatis.generator.utils.CommentUtils;
import org.mybatis.generator.utils.MethodUtils;

/**
 * Description: controller方法生成
 * Author: guos
 * Date: 2019/1/31 12:37
 **/
public class ControllerGen {

    private String responseMethod;

    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;


    private Context context;

    public ControllerGen() {
    }

    public ControllerGen(Context context, String responseMethod, boolean enableLogger) {
        this.context = context;
        this.responseMethod = responseMethod;
        this.enableLogger = enableLogger;
    }

    /**
     * 添加方法
     */
    public Method selectByPrimaryKey(FullyQualifiedJavaType businessType, IntrospectedTable introspectedTable, String alias) {
        String id = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();
        Method method = new Method();
        method.setName(alias);
        String voName = CommonGen.getObjectWithSuffix(introspectedTable.getDomainObjectName(), CommonConstant.VO_SUFFIX);
        method.setReturnType(MethodUtils.getResponseType(responseMethod, voName));
        method.addAnnotation(AnnotationUtils.generateAnnotation("@GetMapping", "{" + id + "}"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@PathVariable(\"" + id + "\")"), "Integer " + id));
        //CommonGen.setMethodPrimaryKey(introspectedTable, method);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        StringBuilder sb = new StringBuilder();
        sb.append(CommonGen.getShortName(businessType));
        sb.append(alias + "(" + id + ")");
        String resMethod = MethodUtils.getResponseMethod(responseMethod, 0);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, id);
        }
        method.addBodyLine("return " + resMethod.replace("$", sb.toString()) + ";");
        return method;
    }


    /**
     * Description:新增或修改
     * param serviceType
     * param introspectedTable
     * param alias
     * Author: guos
     * Date: 2019/1/31 14:33
     * Return:
     **/
    public Method insertOrUpdate(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias) {
        String id = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();
        Method method = new Method();
        method.setName(alias);
        String poName = introspectedTable.getDomainObjectName();
        String aoName = this.getAoName(poName);
        String remarks = introspectedTable.getRemarks();
        method.setReturnType(MethodUtils.getResponseType(responseMethod, "Object"));
        String param = MethodUtils.toLowerCase(aoName);
        if (MethodEnum.SAVE.getValue().equals(alias)) {
            method.addAnnotation(AnnotationUtils.generateAnnotation("@PostMapping", null));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("@RequestBody"), aoName + " " + param));
        } else {
            method.addAnnotation(AnnotationUtils.generateAnnotation("@PutMapping", "{" + id + "}"));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("@PathVariable(\"" + id + "\")"), "Integer " + id));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("@RequestBody"), aoName + " " + param));
        }
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        StringBuilder sb = new StringBuilder();
        sb.append(CommonGen.getShortName(serviceType));
        sb.append(alias);
        sb.append("(");
        sb.append(param);
        sb.append(")");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, param);
        }
        String res;
        if (MethodEnum.SAVE.getValue().equals(alias)) {
            res = sb.toString() + " > 0 ? " + MethodUtils.getResponseMethod(responseMethod, 0).replace("$", CommentUtils.getTransferredStr(remarks + "添加成功")) + ": " + MethodUtils.getResponseMethod(responseMethod, 1).replace("$", CommentUtils.getTransferredStr(remarks + "添加失败"));
        } else {
            method.addBodyLine(param + ".setId(" + id + ");");
            res = sb.toString() + " > 0 ? " + MethodUtils.getResponseMethod(responseMethod, 0).replace("$", CommentUtils.getTransferredStr(remarks + "更新成功")) + ": " + MethodUtils.getResponseMethod(responseMethod, 1).replace("$", CommentUtils.getTransferredStr(remarks + "更新失败"));
        }
        method.addBodyLine("return " + res + ";");
        return method;
    }


    /**
     * 添加方法
     */
    public Method listByCondition(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias) {
        Method method = new Method();
        method.setName(alias);
        String poName = introspectedTable.getDomainObjectName();
        String aoName = this.getAoName(poName);
        String voName = this.getVoName(poName);
        method.setReturnType(MethodUtils.getResponseType(responseMethod, "List<" + voName + ">"));
        method.addAnnotation(AnnotationUtils.generateAnnotation("@GetMapping", null));
        CommonGen.setMethodParameter(method, poName, context.getProp(ExtendModelPlugin.class.getName(), "aoSuffix"));
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        String paramVo = MethodUtils.toLowerCase(aoName);
        String methodPrefix = CommonGen.getShortName(serviceType);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, paramVo);
        }
        method.addBodyLine("return " + methodPrefix + alias + "(" + paramVo + ", this.getPageNum(), this.getPageSize());");
        return method;
    }

    private String getAoName(String poName) {
        return CommonGen.getObjectWithSuffix(poName, context.getProp(ExtendModelPlugin.class.getName(), "aoSuffix"));
    }

    private String getVoName(String poName) {
        return CommonGen.getObjectWithSuffix(poName, context.getProp(ExtendModelPlugin.class.getName(), "voSuffix"));
    }
}
