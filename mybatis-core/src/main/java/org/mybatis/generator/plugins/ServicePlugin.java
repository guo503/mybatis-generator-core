package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
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
 * service生成插件
 * <p>
 * guos
 * 2019/1/17 11:51
 **/
public class ServicePlugin extends BasePlugin {

    private FullyQualifiedJavaType daoType;
    private FullyQualifiedJavaType interfaceType;
    private String servicePack;
    private String serviceImplPack;
    private String serviceProject;
    private String serviceImplProject;

    private String serviceSuffix;


    public ServicePlugin() {
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
        this.serviceSuffix = context.getProp(className, "serviceSuffix");
        this.servicePack = context.getPPVal(className, "servicePack");
        this.serviceImplPack = context.getPPVal(className, "serviceImplPack");
        this.serviceProject = context.getPPVal(className, "serviceProject");
        this.serviceImplProject = context.getPPVal(className, "serviceImplProject");
        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {
        String domainObjectName = introspectedTable.getDomainObjectName();
        //是否生成service
        boolean generatorService = StringUtility.isTrue(context.getTableProp(domainObjectName, KeyConst.ENABLE_SERVICE));
        if (!generatorService) {//是否生成service
            return new ArrayList<>();
        }

        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(this.pojoUrl + ".", "");
        String servicePath = servicePack + "." + tableName + serviceSuffix;
        String serviceImplPath = serviceImplPack + "." + tableName + serviceSuffix + "Impl";

        interfaceType = new FullyQualifiedJavaType(servicePath);
        daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(serviceImplPath);
        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;
        String serviceFilePath = serviceProject + LocalFileUtils.getPath(servicePath) + suffix;
        String serviceImplFilePath = serviceImplProject + LocalFileUtils.getPath(serviceImplPath) + suffix;

        List<GeneratedJavaFile> manageFiles = new ArrayList<>();
        List<GeneratedJavaFile> manageImplFiles = new ArrayList<>();

        Interface interface1 = new Interface(interfaceType);
        interface1.addSuperInterface(new FullyQualifiedJavaType(iService.getShortName() + "<" + domainObjectName + ">"));
        interface1.addImportedType(iService);

        TopLevelClass topLevelClass = new TopLevelClass(serviceType);
        topLevelClass.setSuperClass(new FullyQualifiedJavaType(serviceImpl.getShortName() + "<" + daoType.getShortName() + ", " + domainObjectName + ">"));
        topLevelClass.addImportedType(serviceImpl);

        Files.deleteIfExists(Paths.get(serviceFilePath));
        // 导入必须的类
        addImport(interface1, null);
        interface1.addImportedType(MethodGeneratorUtils.getPoType(context, introspectedTable));
        // 接口
        addService(interface1, manageFiles);
        //添加接口注释
        CommentUtils.addGeneralInterfaceComment(interface1, introspectedTable);
        List<GeneratedJavaFile> files = new ArrayList<>(manageFiles);
        Files.deleteIfExists(Paths.get(serviceImplFilePath));

        // 导入必须的类
        addImport(null, topLevelClass);
        //添加类注释
        CommentUtils.addGeneralClassComment(topLevelClass, introspectedTable);
        // 实现类
        addServiceImpl(topLevelClass, manageImplFiles);
        files.addAll(manageImplFiles);
        return files;
    }

    /**
     * add interface
     *
     * @param files
     */
    protected void addService(Interface interface1, List<GeneratedJavaFile> files) {

        interface1.setVisibility(JavaVisibility.PUBLIC);
        //此外报错[已修2016-03-22，增加:"context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, serviceProject, fileEncoding, context.getJavaFormatter());
        files.add(file);
    }


    /**
     * add implements class
     *
     * @param files
     */
    protected void addServiceImpl(TopLevelClass topLevelClass, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // set implements interface
        topLevelClass.addSuperInterface(interfaceType);

        if (enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(service);
        }
        //添加Log属性
        if (enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }
        // add import dao
        //addField(topLevelClass, tableName);

        //此外报错[已修2016-03-22，增加:",context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, serviceImplProject, fileEncoding, context.getJavaFormatter());
        files.add(file);
    }

    /**
     * 添加字段
     *
     * @param topLevelClass
     */
    protected void addField(TopLevelClass topLevelClass) {
        // add dao
        Field field = new Field();
        field.setName(MethodUtils.toLowerCase(daoType.getShortName())); // set var name
        topLevelClass.addImportedType(daoType);
        field.setType(daoType); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        if (enableAnnotation) {
            field.addAnnotation("@Autowired");
        }
        field.addJavaDocLine("");
        topLevelClass.addField(field);
    }


    /**
     * import must class
     */
    private void addImport(Interface interfaces, TopLevelClass topLevelClass) {
        if (interfaces != null) {
            interfaces.addImportedType(pojoType);
        }
        if (topLevelClass != null) {
            topLevelClass.addImportedType(daoType);
            topLevelClass.addImportedType(interfaceType);
            topLevelClass.addImportedType(pojoType);
            if (enableLogger) {
                topLevelClass.addImportedType(slf4jLogger);
                topLevelClass.addImportedType(slf4jLoggerFactory);
            }
            if (enableAnnotation) {
                topLevelClass.addImportedType(service);
            }
        }
    }
}
