package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.KeyConst;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.method.CommonGen;
import org.mybatis.generator.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Description:controller生成插件
 * Author: guos
 * Date: 2019/1/30 14:26
 **/
public class ControllerPlugin extends BasePlugin {

    private FullyQualifiedJavaType serviceType;

    /**
     * controller包路径
     */
    private String controllerPack;

    /**
     * controller所在模块
     */
    private String controllerProject;

    /**
     * controller类后缀
     */
    private String controllerSuffix;

    /**
     * business包路径
     */
    private String servicePack;

    /**
     * business类后缀
     */
    private String serviceSuffix;

    protected FullyQualifiedJavaType baseController;


    public ControllerPlugin() {
        super();
        className = this.getClass().getName();
    }

    /**
     * 读取配置文件
     */
    @Override
    public boolean validate(List<String> warnings) {
        //是否生成logger
        enableLogger = StringUtility.isTrue(context.getProp(className, "enableLogger"));
        String enableAnnotationStr = context.getProp(className, "enableAnnotation");
        if (StringUtility.stringHasValue(enableAnnotationStr)) {
            enableAnnotation = StringUtility.isTrue(enableAnnotationStr);
        }
        this.controllerProject = context.getPPVal(className, "controllerProject");
        this.controllerPack = context.getPPVal(className, "controllerPack");
        this.controllerSuffix = context.getProp(className, "controllerSuffix");
        this.baseController = new FullyQualifiedJavaType(context.getProp(className, "baseController"));

        this.servicePack = context.getPPVal(ServicePlugin.class.getName(), "servicePack");
        this.serviceSuffix = context.getProp(ServicePlugin.class.getName(), "serviceSuffix");

        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {
        String domainObjectName = introspectedTable.getDomainObjectName();
        //是否生成controller
        boolean generatorController = StringUtility.isTrue(context.getTableProp(domainObjectName, KeyConst.ENABLE_CONTROLLER));
        if (!generatorController) {//是否生成service
            return new ArrayList<>();
        }

        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        List<GeneratedJavaFile> files = new ArrayList<>();

        String serviceName = domainObjectName + serviceSuffix;
        //business全路径
        serviceType = new FullyQualifiedJavaType(servicePack + "." + serviceName);


        //vo全路径
        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(MethodUtils.getFullVoName(domainObjectName, voPack, voSuffix));

        String controllerPath = controllerPack + "." + domainObjectName + controllerSuffix;
        FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType(controllerPath);

        String controllerFilePath = controllerProject + LocalFileUtils.getPath(controllerPath);
        Files.deleteIfExists(Paths.get(controllerFilePath));
        //controller
        TopLevelClass controllerClass = new TopLevelClass(controllerType);
        String baseClass = baseController.getShortName() + "<" + serviceName + "," + domainObjectName + "," + MethodUtils.getShortVoName(domainObjectName, voSuffix) + ">";
        controllerClass.setSuperClass(MethodUtils.getClassName(baseClass));
        controllerClass.addImportedType(baseController);
        controllerClass.addImportedType(serviceType);
        //controllerClass.addImportedType("org.springframework.web.bind.annotation.GetMapping");
        //controllerClass.addImportedType(IPage);
        //生成日志信息
        if (enableLogger) {
            controllerClass.addImportedType(slf4jLogger);
            controllerClass.addImportedType(slf4jLoggerFactory);
        }
        controllerClass.addImportedType(voType);
        controllerClass.addImportedType(pojoType);
        controllerClass.addImportedType("org.springframework.web.bind.annotation.*");
        CommentUtils.addControllerClassComment(controllerClass, introspectedTable);

        //添加方法
        //controllerClass.addMethod(this.listByCondition(introspectedTable, "list"));

        addController(controllerClass, introspectedTable, files);

        return files;
    }


    protected void addController(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        if (this.enableAnnotation) {
            topLevelClass.addAnnotation("@RestController");
            topLevelClass.addAnnotation(AnnotationUtils.generateAnnotation("@RequestMapping", MethodUtils.humpToMiddleLine(introspectedTable.getTableName())));
        }

        if (this.enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }

        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.controllerProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }


    public Method listByCondition(IntrospectedTable introspectedTable, String alias) {
        Method method = new Method();
        method.setName(alias);
        method.addAnnotation("@GetMapping(\"/list\")");
        String poName = introspectedTable.getDomainObjectName();
        String queryName = MethodUtils.getShortVoName(poName, CommonConstant.VO_SUFFIX);
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(IPage.getShortName() + "<" + poName + ">");
        method.setReturnType(returnType);
        CommonGen.setMethodParameter(method, queryName);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        String paramVo = MethodUtils.toLowerCase(queryName);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, paramVo);
        }
        method.addBodyLine("return baseService.list(" + paramVo + ", this.getPageNum(), this.getPageSize());");
        return method;
    }


    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType returnType = method.getReturnType();
        return true;
    }
}
