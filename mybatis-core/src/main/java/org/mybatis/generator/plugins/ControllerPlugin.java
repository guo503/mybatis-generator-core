package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.constant.KeyConst;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.method.ControllerGen;
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

    private FullyQualifiedJavaType businessType;

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
     * vo包路径
     */
    private String queryPack;

    /**
     * vo类后缀
     */
    private String querySuffix;

    /**
     * 返回类方法
     */
    private String responseMethod;

    /**
     * 要继承的基础controller
     */
    private String baseController;

    /**
     * business包路径
     */
    private String businessPack;

    /**
     * business类后缀
     */
    private String businessSuffix;


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

        this.businessPack = context.getPPVal(BusinessPlugin.class.getName(), "businessPack");
        this.businessSuffix = context.getProp(BusinessPlugin.class.getName(), "businessSuffix");

        this.queryPack = context.getPPVal(ExtendModelPlugin.class.getName(), "queryPack");
        this.querySuffix = context.getProp(ExtendModelPlugin.class.getName(), "querySuffix");


        this.responseMethod = context.getProp(className, "responseMethod");

        this.baseController = context.getProp(className, "baseController");
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

        if (!StringUtility.stringHasValue(responseMethod)) {
            throw new RuntimeException(responseMethod + "不能为空");
        }

        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        List<GeneratedJavaFile> files = new ArrayList<>();

        //business全路径
        businessType = new FullyQualifiedJavaType(businessPack + "." + domainObjectName + businessSuffix);

        //vo全路径
        FullyQualifiedJavaType queryType = new FullyQualifiedJavaType(this.queryPack + "." + domainObjectName + this.querySuffix);
        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(MethodUtils.getFullVoName(domainObjectName, voPack, voSuffix));

        String controllerPath = controllerPack + "." + domainObjectName + controllerSuffix;
        FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType(controllerPath);

        String controllerFilePath = controllerProject + LocalFileUtils.getPath(controllerPath);
        Files.deleteIfExists(Paths.get(controllerFilePath));
        //controller
        TopLevelClass controllerClass = new TopLevelClass(controllerType);
        if (StringUtility.stringHasValue(baseController)) {
            controllerClass.setSuperClass(MethodUtils.getClassName(baseController));
            controllerClass.addImportedType(new FullyQualifiedJavaType(baseController));
        } else {
            controllerClass.addAnnotation("@CrossOrigin");
        }
        //生成日志信息
        if (enableLogger) {
            controllerClass.addImportedType(slf4jLogger);
            controllerClass.addImportedType(slf4jLoggerFactory);
        }
        controllerClass.addImportedType(queryType);
        controllerClass.addImportedType(voType);
        controllerClass.addImportedType(pojoType);
        controllerClass.addImportedType(listType);
        controllerClass.addImportedType("org.springframework.web.bind.annotation.*");
        if (StringUtility.stringHasValue(responseMethod)) {
            FullyQualifiedJavaType response = new FullyQualifiedJavaType(MethodUtils.getFullClass(responseMethod, ":"));
            controllerClass.addImportedType(response);
        }
        CommentUtils.addControllerClassComment(controllerClass, introspectedTable);
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

        ClassUtils.addField(topLevelClass, this.businessType, null);
        ControllerGen controllerGen = new ControllerGen(context, this.responseMethod, this.enableLogger);
        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.selectByPrimaryKey))) {
            topLevelClass.addMethod(controllerGen.selectByPrimaryKey(this.businessType, introspectedTable, this.selectByPrimaryKey));
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.insertSelective))) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(this.businessType, introspectedTable, this.insertSelective));
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.updateByPrimaryKeySelective))) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(this.businessType, introspectedTable, this.updateByPrimaryKeySelective));
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.listByCondition))) {
            topLevelClass.addMethod(controllerGen.listByCondition(this.businessType, introspectedTable, this.listByCondition));
        }

        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.controllerProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }


    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType returnType = method.getReturnType();
        return true;
    }
}
