package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.KeyConst;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.method.BusinessGen;
import org.mybatis.generator.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description: 自定义方法生成
 * Author: guos
 * Date: 2019/2/1 11:31
 **/
public class BusinessPlugin extends BasePlugin {

    private FullyQualifiedJavaType serviceType;

    private FullyQualifiedJavaType interfaceType;

    /**
     * service插件类
     **/
    PluginConfiguration servicePlugin;

    /**
     * business包路径
     */
    private String businessPack;

    /**
     * business所在模块
     */
    private String businessProject;

    /**
     * business包路径
     */
    private String businessImplPack;

    /**
     * business所在模块
     */
    private String businessImplProject;

    /**
     * business类后缀
     */
    private String businessSuffix;

    /**
     * 是否生成doBatch方法
     */
    private String doBatchMethod = null;

    /**
     * 返回类方法
     */
    private String responseMethod;

    /**
     * 远程注入注解
     **/
    private String remoteResource;

    /**
     * 是否启用乐观锁,只有versions配置才行
     */
    private boolean enableVersions;

    /**
     * 乐观锁列名
     */
    private String versions;

    /**
     * 对象转换类
     */
    private String modelConvertUtils;


    public BusinessPlugin() {
        super();
        className = this.getClass().getName();
    }


    @Override
    public boolean validate(List<String> warnings) {
        //是否生成logger
        enableLogger = StringUtility.isTrue(context.getProp(className, "enableLogger"));
        String enableAnnotationStr = context.getProp(className, "enableAnnotation");
        if (StringUtility.stringHasValue(enableAnnotationStr)) {
            enableAnnotation = StringUtility.isTrue(enableAnnotationStr);
        }
        this.doBatchMethod = context.getProp(className, MethodEnum.DO_BATCH.getName());
        this.businessSuffix = context.getProp(className, "businessSuffix");

        this.responseMethod = context.getProp(ControllerPlugin.class.getName(), "responseMethod");

        this.modelConvertUtils = context.getProp(className, "modelConvertUtils");
        this.businessProject = context.getPPVal(className, "businessProject");
        this.businessPack = context.getPPVal(className, "businessPack");
        this.businessSuffix = context.getProp(className, "businessSuffix");

        this.businessImplProject = context.getPPVal(className, "businessImplProject");
        this.businessImplPack = context.getPPVal(className, "businessImplPack");

        this.remoteResource = context.getProp(className, "remoteResource");

        this.exceptionPack = context.getProp(className, "exceptionPack");

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {
        String domainObjectName = introspectedTable.getDomainObjectName();
        //是否生成business
        boolean generatorBusiness = StringUtility.isTrue(context.getTableProp(domainObjectName, KeyConst.ENABLE_BUSINESS));
        //乐观锁列
        this.versions = context.getTableProp(domainObjectName, "versionCol");
        this.enableVersions = context.isEnableTableProp(domainObjectName, "versionCol");


        if (!generatorBusiness) {//是否生成service
            return new ArrayList<>();
        }

        List<GeneratedJavaFile> businessFiles = new ArrayList<>();
        List<GeneratedJavaFile> businessImplFiles = new ArrayList<>();

        servicePlugin = ContextUtils.getPlugin(context, CommonConstant.SERVICE_PLUGIN);
        if (servicePlugin == null) {
            throw new RuntimeException("service插件存在");
        }

        if (!StringUtility.stringHasValue(responseMethod)) {
            throw new RuntimeException(responseMethod + "不能为空");
        }

        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        //service全路径
        String servicePack = context.getPPVal(ServicePlugin.class.getName(), "servicePack");
        String serviceName = domainObjectName + context.getProp(ServicePlugin.class.getName(), "serviceSuffix");
        serviceType = new FullyQualifiedJavaType(servicePack + "." + serviceName);
        String businessName = domainObjectName + this.businessSuffix;
        String businessPath = businessPack + "." + businessName;
        String businessImplPath = businessImplPack + "." + businessName + "Impl";

        interfaceType = new FullyQualifiedJavaType(businessPath);
        FullyQualifiedJavaType businessImplType = new FullyQualifiedJavaType(businessImplPath);

        //查询条件类
        String conditionType = context.getProp(ExtendModelPlugin.class.getName(), CommonConstant.CONDITION);

        Interface interface1 = new Interface(interfaceType);
        TopLevelClass businessImplClass = new TopLevelClass(businessImplType);
        businessImplClass.addImportedType(new FullyQualifiedJavaType(conditionType));
        FullyQualifiedJavaType responseType = new FullyQualifiedJavaType(MethodUtils.getFullClass(responseMethod, ":"));
        businessImplClass.addImportedType(responseType);
        interface1.addImportedType(responseType);

        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(MethodUtils.getFullQueryName(domainObjectName, voPack, voSuffix));
        FullyQualifiedJavaType pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);
        FullyQualifiedJavaType queryType = new FullyQualifiedJavaType(MethodUtils.getFullQueryName(domainObjectName, queryPack, querySuffix));

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;

        String businessFilePath = businessProject + LocalFileUtils.getPath(businessPath) + suffix;
        String businessImplFilePath = businessImplProject + LocalFileUtils.getPath(businessImplPath) + suffix;

        Files.deleteIfExists(Paths.get(businessFilePath));
        interface1.addImportedType(listType);
        interface1.addImportedType(pojoType);
        interface1.addImportedType(voType);
        interface1.addImportedType(queryType);
        this.addBusiness(interface1, introspectedTable, businessFiles);
        CommentUtils.addGeneralInterfaceComment(interface1, introspectedTable);
        List<GeneratedJavaFile> files = new ArrayList<>(businessFiles);
        if (this.enableLogger) {
            businessImplClass.addImportedType(this.slf4jLogger);
            businessImplClass.addImportedType(this.slf4jLoggerFactory);
        }
        Files.deleteIfExists(Paths.get(businessImplFilePath));
        businessImplClass.addImportedType(voType);
        businessImplClass.addImportedType(queryType);
        businessImplClass.addImportedType(pojoType);
        businessImplClass.addImportedType(listType);
        businessImplClass.addImportedType(this.interfaceType);
        businessImplClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.BeanUtils"));
        businessImplClass.addImportedType(new FullyQualifiedJavaType("com.google.common.collect.*"));
        businessImplClass.addImportedType(new FullyQualifiedJavaType("java.util.stream.Collectors"));
        if (StringUtility.stringHasValue(this.exceptionPack)) {
            FullyQualifiedJavaTypeUtils.importType((Interface) null, businessImplClass, this.exceptionPack);
        }

        this.addBusinessImpl(businessImplClass, introspectedTable, businessImplFiles);
        CommentUtils.addBusinessClassComment(businessImplClass, introspectedTable);
        files.addAll(businessImplFiles);
        return files;

    }

    protected void addBusiness(Interface interface1, IntrospectedTable introspectedTable, List<GeneratedJavaFile> files) {
        interface1.setVisibility(JavaVisibility.PUBLIC);
        this.addMethods(interface1, null, introspectedTable, true);
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, this.businessProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }

    protected void addBusinessImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addSuperInterface(this.interfaceType);
        if (enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(service);
        }
        if (this.enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }

        ClassUtils.addField(topLevelClass, this.serviceType, this.remoteResource);
        this.addMethods(null, topLevelClass, introspectedTable, false);
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.businessImplProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }


    private void addMethods(Interface interface1, TopLevelClass topLevelClass, IntrospectedTable introspectedTable, boolean isInterface) {
        BusinessGen businessGen = new BusinessGen(context, this.responseMethod, this.modelConvertUtils, this.enableLogger);
        Method method;
        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.selectByPrimaryKey))) {
            method = businessGen.selectByPrimaryKey(this.serviceType, introspectedTable, this.selectByPrimaryKey);
            this.addMethod(method, interface1, topLevelClass, isInterface);
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.insertSelective))) {
            method = businessGen.insertOrUpdate(this.serviceType, introspectedTable, this.insertSelective, this.exceptionPack, this.versions, this.enableVersions);
            this.addMethod(method, interface1, topLevelClass, isInterface);
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.deleteByCondition))) {
            method = businessGen.delete(this.serviceType, introspectedTable, this.deleteByCondition);
            this.addMethod(method, interface1, topLevelClass, isInterface);
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.updateByPrimaryKeySelective))) {
            method = businessGen.insertOrUpdate(this.serviceType, introspectedTable, this.updateByPrimaryKeySelective, this.exceptionPack, this.versions, this.enableVersions);
            this.addMethod(method, interface1, topLevelClass, isInterface);
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.listByCondition))) {
            method = businessGen.listByCondition(this.serviceType, introspectedTable, this.listByCondition);
            this.addMethod(method, interface1, topLevelClass, isInterface);
            if (Objects.nonNull(topLevelClass)) {
                topLevelClass.addImportedType(MethodUtils.getFullQueryName(introspectedTable.getDomainObjectName(), queryPack, querySuffix));
            }
        }

        if (context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.countByCondition))) {
            method = businessGen.count(this.serviceType, introspectedTable, this.countByCondition);
            this.addMethod(method, interface1, topLevelClass, isInterface);
            if (Objects.nonNull(topLevelClass)) {
                topLevelClass.addImportedType(MethodUtils.getFullQueryName(introspectedTable.getDomainObjectName(), queryPack, querySuffix));
            }
        }

        if (context.isCustomEnable(BusinessPlugin.class.getName(), MethodEnum.getNameByValue(this.doBatchMethod))) {
            method = businessGen.doBatch(this.serviceType, introspectedTable, MethodEnum.DO_BATCH.getValue());
            this.addMethod(method, interface1, topLevelClass, isInterface);
        }
    }

    private void addMethod(Method method, Interface interface1, TopLevelClass topLevelClass, boolean isInterface) {
        if (isInterface) {
            MethodUtils.clear(method);
            interface1.addMethod(method);
        } else {
            method.addAnnotation("@Override");
            topLevelClass.addImportedType(objectsType);
            topLevelClass.addMethod(method);
            if (Objects.equals(method.getName(), this.doBatchMethod)) {
                topLevelClass.addImportedType("org.springframework.util.CollectionUtils");
                topLevelClass.addImportedType("mybatis.core.page.Page");
            }
            if (Objects.equals(method.getName(), this.listByCondition)) {
                topLevelClass.addImportedType(modelConvertUtils);
            }
        }
    }
}
