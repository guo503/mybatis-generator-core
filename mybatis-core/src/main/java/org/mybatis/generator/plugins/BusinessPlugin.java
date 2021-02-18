package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.KeyConst;
import org.mybatis.generator.internal.util.StringUtility;
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
public class BusinessPlugin extends BasePlugin {

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

        this.businessSuffix = context.getProp(className, "businessSuffix");
        this.businessProject = context.getPPVal(className, "businessProject");
        this.businessPack = context.getPPVal(className, "businessPack");
        this.businessSuffix = context.getProp(className, "businessSuffix");

        this.businessImplProject = context.getPPVal(className, "businessImplProject");
        this.businessImplPack = context.getPPVal(className, "businessImplPack");

        this.exceptionPack = context.getProp(className, "exceptionPack");

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {
        String domainObjectName = introspectedTable.getDomainObjectName();
        //是否生成business
        boolean generatorBusiness = StringUtility.isTrue(context.getTableProp(domainObjectName, KeyConst.ENABLE_BUSINESS));
        if (!generatorBusiness) {//是否生成service
            return new ArrayList<>();
        }

        List<GeneratedJavaFile> businessFiles = new ArrayList<>();
        List<GeneratedJavaFile> businessImplFiles = new ArrayList<>();

        servicePlugin = ContextUtils.getPlugin(context, CommonConstant.SERVICE_PLUGIN);
        if (servicePlugin == null) {
            throw new RuntimeException("service插件存在");
        }

        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        //service全路径
        String servicePack = context.getPPVal(ServicePlugin.class.getName(), "servicePack");
        String serviceName = domainObjectName + context.getProp(ServicePlugin.class.getName(), "serviceSuffix");
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(servicePack + "." + serviceName);
        String businessName = domainObjectName + this.businessSuffix;
        String businessPath = businessPack + "." + businessName;
        String businessImplPath = businessImplPack + "." + businessName + "Impl";

        interfaceType = new FullyQualifiedJavaType(businessPath);
        FullyQualifiedJavaType businessImplType = new FullyQualifiedJavaType(businessImplPath);

        Interface interface1 = new Interface(interfaceType);
        TopLevelClass businessImplClass = new TopLevelClass(businessImplType);

        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(MethodUtils.getFullVoName(domainObjectName, voPack, voSuffix));
        FullyQualifiedJavaType pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);
        FullyQualifiedJavaType queryType = new FullyQualifiedJavaType(MethodUtils.getFullQueryName(domainObjectName, queryPack, querySuffix));

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;

        String businessFilePath = businessProject + LocalFileUtils.getPath(businessPath) + suffix;
        String businessImplFilePath = businessImplProject + LocalFileUtils.getPath(businessImplPath) + suffix;

        Files.deleteIfExists(Paths.get(businessFilePath));
        interface1.addImportedType(pojoType);
        interface1.addImportedType(voType);
        interface1.addImportedType(queryType);
        String baseInterface = IBusiness.getShortName() + "<" + domainObjectName + "," + MethodUtils.getShortQueryName(domainObjectName, querySuffix) + "," + MethodUtils.getShortVoName(domainObjectName, voSuffix) + ">";
        interface1.addSuperInterface(new FullyQualifiedJavaType(baseInterface));
        interface1.addImportedType(IBusiness);
        this.addBusiness(interface1, businessFiles);
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
        businessImplClass.addImportedType(this.interfaceType);
        String baseClass = businessImpl.getShortName() + "<" + serviceName + "," + domainObjectName + "," + MethodUtils.getShortQueryName(domainObjectName, querySuffix) + "," + MethodUtils.getShortVoName(domainObjectName, voSuffix) + ">";
        businessImplClass.setSuperClass(baseClass);
        businessImplClass.addImportedType(businessImpl);
        businessImplClass.addImportedType(serviceType);

        this.addBusinessImpl(businessImplClass, businessImplFiles);
        CommentUtils.addBusinessClassComment(businessImplClass, introspectedTable);
        files.addAll(businessImplFiles);
        return files;

    }

    protected void addBusiness(Interface interface1, List<GeneratedJavaFile> files) {
        interface1.setVisibility(JavaVisibility.PUBLIC);
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, this.businessProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }

    protected void addBusinessImpl(TopLevelClass topLevelClass, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addSuperInterface(this.interfaceType);
        if (enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(service);
        }
        if (this.enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.businessImplProject, this.fileEncoding, this.context.getJavaFormatter());
        files.add(file);
    }
}
