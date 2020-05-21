package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;
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
public class ControllerPlugin extends PluginAdapter {

    private final FullyQualifiedJavaType slf4jLogger;
    private final FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType classAnnotation;

    private FullyQualifiedJavaType businessType;


    /**
     * 是否添加注解
     */
    private boolean enableAnnotation = true;

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
    private String aoPack;

    /**
     * vo类后缀
     */
    private String aoSuffix;

    /**
     * 返回类方法
     */
    private String responseMethod;


    /**
     * 要继承的基础controller
     */
    private String baseController;

    /**
     * 新增方法
     **/
    private String insertMethod = null;

    /**
     * 更新方法
     **/
    private String updateMethod = null;

    /**
     * 单个方法
     **/
    private String selectMethod = null;

    /**
     * 条件方法
     **/
    private String listMethod = null;


    /**
     * business包路径
     */
    private String businessPack;

    /**
     * business类后缀
     */
    private String businessSuffix;

    /**
     * 编码
     **/
    private String fileEncoding;


    /**
     * 是否生成controller
     **/
    private boolean generatorController = false;


    private final String className;

    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;

    public ControllerPlugin() {
        super();
        // default is slf4j
        slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        className = this.getClass().getName();
    }

    /**
     * 读取配置文件
     */
    @Override
    public boolean validate(List<String> warnings) {
        String enableAnnotation = properties.getProperty("enableAnnotation");
        this.controllerProject = context.getPPVal(className, "controllerProject");
        this.controllerPack = context.getPPVal(className, "controllerPack");
        this.controllerSuffix = context.getProp(className, "controllerSuffix");

        this.businessPack = context.getPPVal(BusinessPlugin.class.getName(), "businessPack");
        this.businessSuffix = context.getProp(BusinessPlugin.class.getName(), "businessSuffix");

        this.aoPack = context.getPPVal(ExtendModelPlugin.class.getName(), "aoPack");
        this.aoSuffix = context.getProp(ExtendModelPlugin.class.getName(), "aoSuffix");


        this.responseMethod = context.getProp(className, "responseMethod");

        this.baseController = context.getProp(className, "baseController");

        String daoType = BaseMethodPlugin.class.getName();
        this.insertMethod = context.getProp(daoType, MethodEnum.SAVE.getName());
        this.updateMethod = context.getProp(daoType, MethodEnum.UPDATE.getName());
        this.selectMethod = context.getProp(daoType, MethodEnum.GET.getName());
        this.listMethod = context.getProp(daoType, MethodEnum.LIST_BY_CONDITION.getName());
        /**
         * 查询总数
         **/
        String countMethod = context.getProp(daoType, MethodEnum.COUNT_BY_CONDITION.getName());

        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);

        //是否生成logger
        enableLogger = StringUtility.isTrue(context.getProp(className, "enableLogger"));


        if (StringUtility.stringHasValue(enableAnnotation)) {
            this.enableAnnotation = StringUtility.isTrue(enableAnnotation);
        }

        if (this.enableAnnotation) {
            classAnnotation = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.*");
        }
        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {
        String domainObjectName = introspectedTable.getDomainObjectName();
        //是否生成controller
        this.generatorController = StringUtility.isTrue(context.getTableProp(domainObjectName, KeyConst.ENABLE_CONTROLLER));
        if (!generatorController) {//是否生成service
            return new ArrayList<>();
        }

        if (!StringUtility.stringHasValue(responseMethod)) {
            throw new RuntimeException(responseMethod + "不能为空");
        }

        List<GeneratedJavaFile> files = new ArrayList<>();

        //business全路径
        businessType = new FullyQualifiedJavaType(businessPack + "." + domainObjectName + businessSuffix);

        //vo全路径
        FullyQualifiedJavaType aoType = new FullyQualifiedJavaType(this.aoPack + "." + domainObjectName + this.aoSuffix);
        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(context.getPPVal(ExtendModelPlugin.class.getName(), "voPack") + "." + domainObjectName + context.getProp(ExtendModelPlugin.class.getName(), "voSuffix"));

        String controllerPath = controllerPack + "." + domainObjectName + controllerSuffix;
        FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType(controllerPath);

        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("java.util.List");

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
        controllerClass.addImportedType(aoType);
        controllerClass.addImportedType(voType);
        controllerClass.addImportedType(listType);
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
            topLevelClass.addImportedType(this.classAnnotation);
        }

        if (this.enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }

        ClassUtils.addField(topLevelClass, this.businessType, null);
        ControllerGen controllerGen = new ControllerGen(context, this.responseMethod, this.enableLogger);
        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.selectMethod))) {
            topLevelClass.addMethod(controllerGen.selectByPrimaryKey(this.businessType, introspectedTable, this.selectMethod));
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.insertMethod))) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(this.businessType, introspectedTable, this.insertMethod));
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.updateMethod))) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(this.businessType, introspectedTable, this.updateMethod));
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.listMethod))) {
            topLevelClass.addMethod(controllerGen.listByCondition(this.businessType, introspectedTable, this.listMethod));
        }

        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.controllerProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }


    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType returnType = method.getReturnType();
        return true;
    }
}
