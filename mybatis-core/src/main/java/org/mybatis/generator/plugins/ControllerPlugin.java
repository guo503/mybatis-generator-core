package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
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

    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
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
     * 所有的方法
     */
    private List<Method> methods;

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
     * 查询总数
     **/
    private String countMethod = null;

    /**
     * 条件方法
     **/
    private String listByIds = null;


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

    /**
     * businessPlugin插件
     **/
    private PluginConfiguration businessPlugin;

    /**
     * 表配置列表
     */
    private List<TableConfiguration> tableConfigurationList;

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
        methods = new ArrayList<>();
        className = this.getClass().getName();
    }

    /**
     * 读取配置文件
     */
    @Override
    public boolean validate(List<String> warnings) {
        String enableAnnotation = properties.getProperty("enableAnnotation");
        this.controllerProject = this.getCustomValue(className, "controllerProject");
        this.controllerPack = this.getCustomValue(className, "controllerPack");
        this.controllerSuffix = this.getCustomValue(className, "controllerSuffix");

        this.businessPack = this.getCustomValue(BusinessPlugin.class.getName(), "businessPack");
        this.businessSuffix = this.getCustomValue(BusinessPlugin.class.getName(), "businessSuffix");

        this.aoPack = this.getCustomValue(ExtendModelPlugin.class.getName(), "aoPack");
        this.aoSuffix = this.getCustomValue(ExtendModelPlugin.class.getName(), "aoSuffix");


        this.responseMethod = this.getCustomValue(className, "responseMethod");

        this.baseController = this.getCustomValue(className, "baseController");

        String daoType = BaseMethodPlugin.class.getName();
        this.insertMethod = this.getCustomValue(daoType, MethodEnum.SAVE.getName());
        this.updateMethod = this.getCustomValue(daoType, MethodEnum.UPDATE.getName());
        this.selectMethod = this.getCustomValue(daoType, MethodEnum.GET.getName());
        this.listMethod = this.getCustomValue(daoType, MethodEnum.LIST_BY_CONDITION.getName());
        this.countMethod = this.getCustomValue(daoType, MethodEnum.COUNT_BY_CONDITION.getName());

        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);

        //是否生成logger
        enableLogger = StringUtility.isTrue(this.getCustomValue(className, "enableLogger"));


        tableConfigurationList = context.getTableConfigurations();

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

        //是否生成controller
        for (TableConfiguration tableConfiguration : tableConfigurationList) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                this.generatorController = tableConfiguration.isEnableController();
                break;
            }
        }
        if (!generatorController) {//是否生成service
            return new ArrayList<>();
        }

        if (!StringUtility.stringHasValue(responseMethod)) {
            throw new RuntimeException(responseMethod + "不能为空");
        }

        List<GeneratedJavaFile> files = new ArrayList<>();
        //po全路径

        String domainObjectName = introspectedTable.getDomainObjectName();
        //business全路径
        businessType = new FullyQualifiedJavaType(businessPack + "." + domainObjectName + businessSuffix);

        //vo全路径
        FullyQualifiedJavaType aoType = new FullyQualifiedJavaType(this.aoPack + "." + domainObjectName + this.aoSuffix);
        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(this.getCustomValue(ExtendModelPlugin.class.getName(), "voPack") + "." + domainObjectName + this.getCustomValue(ExtendModelPlugin.class.getName(), "voSuffix"));

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
        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.selectMethod))) {
            topLevelClass.addMethod(controllerGen.selectByPrimaryKey(this.businessType, introspectedTable, this.selectMethod));
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.insertMethod))) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(this.businessType, introspectedTable, this.insertMethod));
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.updateMethod))) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(this.businessType, introspectedTable, this.updateMethod));
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.listMethod))) {
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
