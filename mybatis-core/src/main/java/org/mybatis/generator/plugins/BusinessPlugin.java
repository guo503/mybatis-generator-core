package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.method.BusinessGen;
import org.mybatis.generator.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 自定义方法生成
 * Author: guos
 * Date: 2019/2/1 11:31
 **/
public class BusinessPlugin extends PluginAdapter {

    private final FullyQualifiedJavaType slf4jLogger;
    private final FullyQualifiedJavaType slf4jLoggerFactory;

    private FullyQualifiedJavaType serviceType;

    private FullyQualifiedJavaType interfaceType;

    /**
     * 是否添加注解
     */
    private boolean enableAnnotation = true;

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
     * 物理删除
     */
    private String deleteByCondition = null;

    /**
     * 返回类方法
     */
    private String responseMethod;

    /**
     * 编码
     **/
    private String fileEncoding;

    /**
     * 远程注入注解
     **/
    private String remoteResource;

    /**
     * 是否生成business
     **/
    private boolean generatorBusiness = false;

    /**
     * 是否启用乐观锁,只有versions配置才行
     */
    private boolean enableVersions = false;

    /**
     * 乐观锁列名
     */
    private String versions;


    /**
     * 对象转换类
     */
    private String modelConvertUtils;

    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;

    /**
     * 分页类路径
     */
    private String page;


    /**
     * 自定义异常类全路径
     **/
    private String exceptionPack;

    private final String className;


    public BusinessPlugin() {
        super();
        // default is slf4j

        slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        /**
         * 所有的方法
         */
        className = this.getClass().getName();
    }


    @Override
    public boolean validate(List<String> warnings) {
        String enableAnnotation = properties.getProperty("enableAnnotation");

        String daoType = BaseMethodPlugin.class.getName();
        this.insertMethod = this.getCustomValue(daoType, MethodEnum.SAVE.getName());
        this.updateMethod = this.getCustomValue(daoType, MethodEnum.UPDATE.getName());
        this.selectMethod = this.getCustomValue(daoType, MethodEnum.GET.getName());
        this.listMethod = this.getCustomValue(daoType, MethodEnum.LIST_BY_CONDITION.getName());
        this.countMethod = this.getCustomValue(daoType, MethodEnum.COUNT_BY_CONDITION.getName());
        this.doBatchMethod = this.getCustomValue(className, MethodEnum.DO_BATCH.getName());
        this.deleteByCondition = this.getCustomValue(daoType, MethodEnum.REAL_DELETE.getName());

        this.businessSuffix = this.getCustomValue(className, "businessSuffix");

        this.responseMethod = this.getCustomValue(ControllerPlugin.class.getName(), "responseMethod");

        this.modelConvertUtils = this.getCustomValue(className, "modelConvertUtils");
        this.businessProject = context.getPath(className, "businessProject");
        this.businessPack = context.getPack(className, "businessPack");
        this.businessSuffix = this.getCustomValue(className, "businessSuffix");

        this.businessImplProject = context.getPath(className, "businessImplProject");
        this.businessImplPack = context.getPack(className, "businessImplPack");

        this.remoteResource = this.getCustomValue(className, "remoteResource");

        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);

        //是否生成logger
        enableLogger = StringUtility.isTrue(this.getCustomValue(className, "enableLogger"));

        page = context.getProperty("page");

        this.exceptionPack = this.getCustomValue(className, "exceptionPack");

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {

        //是否生成business
        for (TableConfiguration tableConfiguration : context.getTableConfigurations()) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                this.generatorBusiness = tableConfiguration.isEnableBusiness();
                this.versions = tableConfiguration.getVersionCol();
                this.enableVersions = tableConfiguration.isEnableVersions();
                break;
            }
        }

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

        String domainObjectName = introspectedTable.getDomainObjectName();
        //service全路径
        String servicePack = context.getPack(ServicePlugin.class.getName(), "servicePack");
        String serviceName = domainObjectName + this.getCustomValue(ServicePlugin.class.getName(), "serviceSuffix");
        serviceType = new FullyQualifiedJavaType(servicePack + "." + serviceName);
        String businessName = domainObjectName + this.businessSuffix;
        String businessPath = businessPack + "." + businessName;
        String businessImplPath = businessImplPack + "." + businessName + "Impl";

        interfaceType = new FullyQualifiedJavaType(businessPath);
        FullyQualifiedJavaType businessImplType = new FullyQualifiedJavaType(businessImplPath);

        //查询条件类
        String conditionType = this.getCustomValue(ExtendModelPlugin.class.getName(), CommonConstant.CONDITION);

        Interface interface1 = new Interface(interfaceType);
        TopLevelClass businessImplClass = new TopLevelClass(businessImplType);
        businessImplClass.addImportedType(new FullyQualifiedJavaType(conditionType));
        FullyQualifiedJavaType responseType = new FullyQualifiedJavaType(MethodUtils.getFullClass(responseMethod, ":"));
        businessImplClass.addImportedType(responseType);
        interface1.addImportedType(responseType);

        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("java.util.*");

        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(this.getCustomValue(ExtendModelPlugin.class.getName(), "voPack") + "." + domainObjectName + this.getCustomValue(ExtendModelPlugin.class.getName(), "voSuffix"));
        FullyQualifiedJavaType pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);
        FullyQualifiedJavaType aoType = new FullyQualifiedJavaType(this.getCustomValue(ExtendModelPlugin.class.getName(), "aoPack") + "." + domainObjectName + this.getCustomValue(ExtendModelPlugin.class.getName(), "aoSuffix"));

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;

        String businessFilePath = businessProject + LocalFileUtils.getPath(businessPath) + suffix;
        String businessImplFilePath = businessImplProject + LocalFileUtils.getPath(businessImplPath) + suffix;

        String tableName = introspectedTable.getBaseRecordType();

        Files.deleteIfExists(Paths.get(businessFilePath));
        interface1.addImportedType(listType);
        interface1.addImportedType(voType);
        interface1.addImportedType(aoType);
        this.addBusiness(interface1, introspectedTable, businessFiles);
        CommentUtils.addGeneralInterfaceComment(interface1, introspectedTable);
        List<GeneratedJavaFile> files = new ArrayList<>(businessFiles);
        if (this.enableLogger) {
            businessImplClass.addImportedType(this.slf4jLogger);
            businessImplClass.addImportedType(this.slf4jLoggerFactory);
        }
        Files.deleteIfExists(Paths.get(businessImplFilePath));
        businessImplClass.addImportedType(voType);
        businessImplClass.addImportedType(aoType);
        businessImplClass.addImportedType(pojoType);
        businessImplClass.addImportedType(listType);
        businessImplClass.addImportedType(this.interfaceType);
        businessImplClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.BeanUtils"));
        businessImplClass.addImportedType(new FullyQualifiedJavaType("com.google.common.collect.*"));
        if (StringUtility.stringHasValue(this.exceptionPack)) {
            FullyQualifiedJavaTypeUtils.importType((Interface) null, businessImplClass, this.exceptionPack);
        }

        this.addBusinessImpl(businessImplClass, introspectedTable, tableName, businessImplFiles);
        CommentUtils.addBusinessClassComment(businessImplClass, introspectedTable);
        files.addAll(businessImplFiles);
        return files;

    }

    protected void addBusiness(Interface interface1, IntrospectedTable introspectedTable, List<GeneratedJavaFile> files) {
        interface1.setVisibility(JavaVisibility.PUBLIC);
        BusinessGen businessGen = new BusinessGen(context, this.responseMethod, this.modelConvertUtils, this.enableLogger);
        Method method;
        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.selectMethod))) {
            method = businessGen.selectByPrimaryKey(this.serviceType, introspectedTable, this.selectMethod);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.insertMethod))) {
            method = businessGen.insertOrUpdate(this.serviceType, introspectedTable, this.insertMethod, this.exceptionPack, this.versions, this.enableVersions);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.deleteByCondition))) {
            method = businessGen.delete(this.serviceType, introspectedTable, this.deleteByCondition);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.updateMethod))) {
            method = businessGen.insertOrUpdate(this.serviceType, introspectedTable, this.updateMethod, this.exceptionPack, this.versions, this.enableVersions);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.listMethod))) {
            method = businessGen.listByCondition(this.serviceType, introspectedTable, this.listMethod, this.page);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.countMethod))) {
            method = businessGen.count(this.serviceType, introspectedTable, this.countMethod);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.doBatchMethod)) && StringUtility.stringHasValue(this.page)) {
            method = businessGen.doBatch(this.serviceType, introspectedTable, MethodEnum.DO_BATCH.getValue(), this.page);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        GeneratedJavaFile file = new GeneratedJavaFile(interface1, this.businessProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }

    protected void addBusinessImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addSuperInterface(this.interfaceType);
        if (this.enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        }

        if (this.enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }

        ClassUtils.addField(topLevelClass, this.serviceType, this.remoteResource);
        BusinessGen businessGen = new BusinessGen(context, this.responseMethod, this.modelConvertUtils, this.enableLogger);
        Method method;
        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.selectMethod))) {
            method = businessGen.selectByPrimaryKey(this.serviceType, introspectedTable, this.selectMethod);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.insertMethod))) {
            method = businessGen.insertOrUpdate(this.serviceType, introspectedTable, this.insertMethod, this.exceptionPack, this.versions, this.enableVersions);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.deleteByCondition))) {
            method = businessGen.delete(this.serviceType, introspectedTable, this.deleteByCondition);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.updateMethod))) {
            method = businessGen.insertOrUpdate(this.serviceType, introspectedTable, this.updateMethod, this.exceptionPack, this.versions, this.enableVersions);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (StringUtility.stringHasValue(this.listMethod) || StringUtility.stringHasValue(this.countMethod)) {
            method = businessGen.listByCondition(this.serviceType, introspectedTable, this.listMethod, this.page);
            if (StringUtility.stringHasValue(this.modelConvertUtils)) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(this.modelConvertUtils));
            }

            if (StringUtility.stringHasValue(this.page)) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(this.page));
            }

            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.countMethod))) {
            method = businessGen.count(this.serviceType, introspectedTable, this.countMethod);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(this.doBatchMethod)) && StringUtility.stringHasValue(this.page)) {
            method = businessGen.doBatch(this.serviceType, introspectedTable, MethodEnum.DO_BATCH.getValue(), this.page);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
            topLevelClass.addImportedType("org.springframework.util.CollectionUtils");
        }

        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.businessImplProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }
}
