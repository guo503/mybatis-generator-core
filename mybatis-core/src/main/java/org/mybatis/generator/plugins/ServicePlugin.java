package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
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
public class ServicePlugin extends PluginAdapter {

    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType serviceType;
    private FullyQualifiedJavaType daoType;
    private FullyQualifiedJavaType interfaceType;
    private FullyQualifiedJavaType pojoType;
    private FullyQualifiedJavaType listType;
    private FullyQualifiedJavaType autowired;
    private FullyQualifiedJavaType service;
    private FullyQualifiedJavaType returnType;
    private String servicePack;
    private String serviceImplPack;
    private String serviceProject;
    private String serviceImplProject;
    private String pojoUrl;
    /**
     * 是否添加注解
     */
    private boolean enableAnnotation = true;
    private String deleteByCondition;
    private boolean generatorService = false;
    private String insertSelective;
    private String updateByPrimaryKeySelective;
    private String selectByPrimaryKey;
    private String listByIds;
    private String countByCondition;
    private String listByCondition;
    private String count;
    private String list;
    private String fileEncoding;


    private String mapByIds;
    private String map;

    private String listId;

    private String saveAndGet;


    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;


    private String serviceSuffix;


    private final String className;


    public ServicePlugin() {
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

        //是否生成logger
        enableLogger = StringUtility.isTrue(this.getCustomValue(className, "enableLogger"));


        if (StringUtility.stringHasValue(enableAnnotation)) {
            this.enableAnnotation = StringUtility.isTrue(enableAnnotation);
        }

        String daoType = BaseMethodPlugin.class.getName();
        this.serviceSuffix = this.getCustomValue(className, "serviceSuffix");

        this.selectByPrimaryKey = this.getCustomValue(daoType, MethodEnum.GET.getName());
        this.insertSelective = this.getCustomValue(daoType, MethodEnum.SAVE.getName());
        this.updateByPrimaryKeySelective = this.getCustomValue(daoType, MethodEnum.UPDATE.getName());
        this.listByIds = this.getCustomValue(daoType, MethodEnum.LIST_BY_IDS.getName());
        this.listByCondition = this.getCustomValue(daoType, MethodEnum.LIST_BY_CONDITION.getName());
        this.countByCondition = this.getCustomValue(daoType, MethodEnum.COUNT_BY_CONDITION.getName());
        this.map = this.getCustomValue(className, MethodEnum.MAP.getName());
        this.mapByIds = this.getCustomValue(className, MethodEnum.MAP_BY_IDS.getName());
        this.listId = this.getCustomValue(className, MethodEnum.LIST_ID.getName());
        this.saveAndGet = this.getCustomValue(className, MethodEnum.SAVE_AND_GET.getName());
        this.count = this.getCustomValue(daoType, MethodEnum.COUNT.getName());
        this.list = this.getCustomValue(daoType, MethodEnum.LIST.getName());
        this.deleteByCondition = this.getCustomValue(daoType, MethodEnum.DELETE_BY_CONDITION.getName());

        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);
        this.servicePack = this.getCustomValue(className, "servicePack");
        this.serviceImplPack = this.getCustomValue(className, "serviceImplPack");
        this.serviceProject = this.getCustomValue(className, "serviceProject");
        this.serviceImplProject = this.getCustomValue(className, "serviceImplProject");
        this.pojoUrl = context.getJavaModelGeneratorConfiguration().getTargetPackage();

        if (this.enableAnnotation) {
            autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
            service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        }
        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {


        //是否生成business
        for (TableConfiguration tableConfiguration : context.getTableConfigurations()) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                this.generatorService = tableConfiguration.isEnableService();
                break;
            }
        }

        if (!generatorService) {//是否生成service
            return new ArrayList<>();
        }

        // 取Service名称【com.coolead.service.PetService】
        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(this.pojoUrl + ".", "");
        String servicePath = context.getPack(this.getClass().getName(), servicePack) + "." + tableName + serviceSuffix;
        String serviceImplPath = context.getPack(this.getClass().getName(), serviceImplPack) + "." + tableName + serviceSuffix + "Impl";

        interfaceType = new FullyQualifiedJavaType(servicePath);

        // 【com.coolead.mapper.UserMapper】
        daoType = new FullyQualifiedJavaType(context.getPack(ManagePlugin.class.getName(), "managePack") + "." + tableName + context.getPack(ManagePlugin.class.getName(), "manageSuffix"));

        // 【com.coolead.service.impl.PetServiceImpl】logger.info(toLowerCase(daoType.getShortName()));
        serviceType = new FullyQualifiedJavaType(serviceImplPath);

        // 【com.coolead.domain.Pet】
        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        listType = new FullyQualifiedJavaType("java.util.*");

        //查询条件类
        String conditionType = this.getCustomValue(ExtendModelPlugin.class.getName(), CommonConstant.CONDITION);

        //分页查询条件类
        String limitConditionType = this.getCustomValue(ExtendModelPlugin.class.getName(), CommonConstant.LIMIT_CONDITION);

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;
        String serviceFilePath = context.getPath(this.getClass().getName(), serviceProject) + LocalFileUtils.getPath(servicePath) + suffix;
        String serviceImplFilePath = context.getPath(this.getClass().getName(), serviceImplProject) + LocalFileUtils.getPath(serviceImplPath) + suffix;

        List<GeneratedJavaFile> manageFiles = new ArrayList<>();
        List<GeneratedJavaFile> manageImplFiles = new ArrayList<>();

        Interface interface1 = new Interface(interfaceType);
        interface1.addImportedType(new FullyQualifiedJavaType(conditionType));
        interface1.addImportedType(new FullyQualifiedJavaType(limitConditionType));
        TopLevelClass topLevelClass = new TopLevelClass(serviceType);
        topLevelClass.addImportedType(new FullyQualifiedJavaType(conditionType));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(limitConditionType));
        Files.deleteIfExists(Paths.get(serviceFilePath));
        // 导入必须的类
        addImport(interface1, null);
        interface1.addImportedType(MethodGeneratorUtils.getPoType(context, introspectedTable));
        // 接口
        addService(interface1, introspectedTable, tableName, manageFiles);
        //添加接口注释
        CommentUtils.addGeneralInterfaceComment(interface1, introspectedTable);
        List<GeneratedJavaFile> files = new ArrayList<>(manageFiles);
        Files.deleteIfExists(Paths.get(serviceImplFilePath));

        // 导入必须的类
        addImport(null, topLevelClass);
        //添加类注释
        CommentUtils.addGeneralClassComment(topLevelClass, introspectedTable);
        // 实现类
        addServiceImpl(topLevelClass, introspectedTable, tableName, manageImplFiles);
        files.addAll(manageImplFiles);
        return files;
    }

    /**
     * add interface
     *
     * @param tableName
     * @param files
     */
    protected void addService(Interface interface1, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {

        interface1.setVisibility(JavaVisibility.PUBLIC);

        // add method
        Method method;

        method = selectByPrimaryKey(introspectedTable, selectByPrimaryKey, tableName);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = selectByModel(introspectedTable, MethodEnum.GET_ONE.getValue());
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = getOtherInteger(BaseMethodPlugin.class.getName(), insertSelective, introspectedTable, tableName, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = getOtherInteger(this.getClass().getName(), saveAndGet, introspectedTable, tableName, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = getOtherInteger(BaseMethodPlugin.class.getName(), updateByPrimaryKeySelective, introspectedTable, tableName, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        if (StringUtility.stringHasValue(deleteByCondition)) {
            method = delete(introspectedTable, deleteByCondition, tableName, 1);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        method = listByIds(BaseMethodPlugin.class.getName(), introspectedTable, listByIds, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = listByCondition(BaseMethodPlugin.class.getName(), introspectedTable, list, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = countByCondition(introspectedTable, count);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = listByCondition(BaseMethodPlugin.class.getName(), introspectedTable, listByCondition, 4);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = countByCondition(introspectedTable, countByCondition);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = listByCondition(this.getClass().getName(), introspectedTable, listId, 2);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = listByIds(this.getClass().getName(), introspectedTable, mapByIds, 2);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = listByCondition(this.getClass().getName(), introspectedTable, map, 3);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = batchList(null, introspectedTable, MethodEnum.BATCH_LIST.getValue());
        MethodUtils.clear(method);
        interface1.addMethod(method);

        //此外报错[已修2016-03-22，增加:"context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, serviceProject, fileEncoding, context.getJavaFormatter());

        files.add(file);
    }


    /**
     * add implements class
     *
     * @param introspectedTable
     * @param tableName
     * @param files
     */
    protected void addServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // set implements interface
        topLevelClass.addSuperInterface(interfaceType);

        if (enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(service);
        }
        topLevelClass.addImportedType(serviceType);
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.util.*"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.stream.Collectors"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.google.common.collect.*"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.util.Assert"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.transaction.annotation.Transactional"));
        //添加Log属性
        if (enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }
        // add import dao
        addField(topLevelClass, tableName);

        /**
         * type:  pojo 1 ;key 2 ;example 3 ;pojo+example 4
         */


        topLevelClass.addMethod(selectByPrimaryKey(introspectedTable, selectByPrimaryKey, tableName));

        topLevelClass.addMethod(selectByModel(introspectedTable, MethodEnum.GET_ONE.getValue()));

        topLevelClass.addMethod(getOtherInteger(BaseMethodPlugin.class.getName(), insertSelective, introspectedTable, tableName, 1));

        if (StringUtility.stringHasValue(deleteByCondition)) {
            topLevelClass.addMethod(delete(introspectedTable, deleteByCondition, tableName, 1));
        }

        topLevelClass.addMethod(getOtherInteger(this.getClass().getName(), saveAndGet, introspectedTable, tableName, 1));

        topLevelClass.addMethod(getOtherInteger(BaseMethodPlugin.class.getName(), updateByPrimaryKeySelective, introspectedTable, tableName, 1));

        topLevelClass.addMethod(getOtherList(BaseMethodPlugin.class.getName(), listByIds, introspectedTable, tableName, 6));

        topLevelClass.addMethod(getOtherList(BaseMethodPlugin.class.getName(), list, introspectedTable, tableName, 5));

        topLevelClass.addMethod(countByCondition(introspectedTable, count));

        topLevelClass.addMethod(getOtherList(BaseMethodPlugin.class.getName(), listByCondition, introspectedTable, tableName, 8));

        topLevelClass.addMethod(countByCondition(introspectedTable, countByCondition));

        topLevelClass.addMethod(getOtherList(this.getClass().getName(), listId, introspectedTable, tableName, 7));

        topLevelClass.addMethod(getOtherMap(map, introspectedTable, tableName, 7));

        topLevelClass.addMethod(getOtherMap(mapByIds, introspectedTable, tableName, 6));

        topLevelClass.addMethod(batchList(topLevelClass, introspectedTable, MethodEnum.BATCH_LIST.getValue()));

        //此外报错[已修2016-03-22，增加:",context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, serviceImplProject, fileEncoding, context.getJavaFormatter());
        files.add(file);
    }

    /**
     * 添加字段
     *
     * @param topLevelClass
     */
    protected void addField(TopLevelClass topLevelClass, String tableName) {
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
     * 添加方法
     * flag 1:根据id查询
     */
    protected Method selectByPrimaryKey(IntrospectedTable introspectedTable, String alias, String tableName) {
        if (!this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(alias))) {
            return null;
        }
        Method method = new Method();
        method.setName(alias);
        String domainObjectName = introspectedTable.getDomainObjectName();
        method.setReturnType(new FullyQualifiedJavaType(domainObjectName));
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();
        if (columns == null || columns.size() == 0) {
            throw new RuntimeException("请设置表的唯一主键列！");
        }
        String primaryKey = columns.get(0).getJavaProperty();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, primaryKey);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(alias);
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        method.addBodyLine("Assert.notNull(" + primaryKey + ",\"" + primaryKey + "不能为空\");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * 添加方法
     * flag 1:根据id查询
     */
    protected Method selectByModel(IntrospectedTable introspectedTable, String alias) {
        Method method = new Method();
        method.setName(alias);
        String domainObjectName = introspectedTable.getDomainObjectName();
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(domainObjectName);
        method.addParameter(new Parameter(type, lowPo));
        method.setReturnType(new FullyQualifiedJavaType(domainObjectName));
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, lowPo);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(alias);
        sb.append("(");
        sb.append(lowPo);
        sb.append(");");
        method.addBodyLine("Assert.notNull(" + lowPo + ",\"" + lowPo + "不能为空\");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * 添加方法
     * 删除
     */
    protected Method delete(IntrospectedTable introspectedTable, String alias, String tableName, int flag) {
        if (!this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(alias))) {
            return null;
        }
        Method method = new Method();
        method.setName(alias);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();
        if (columns == null || columns.size() == 0) {
            throw new RuntimeException("请设置表的唯一主键列！");
        }
        String primaryKey = columns.get(0).getJavaProperty();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, primaryKey);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        method.addBodyLine("Assert.notNull(" + primaryKey + ",\"" + primaryKey + "不能为空\");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * add method
     */
    protected Method countByCondition(IntrospectedTable introspectedTable, String methodName) {
        if (!this.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        String domainObjectName = introspectedTable.getDomainObjectName();
        String condName = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(domainObjectName));
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addAnnotation("@Override");
        //method.addParameter(new Parameter(this.getShortPojoType(introspectedTable), condName));
        String poName = MethodUtils.toLowerCase(domainObjectName);
        Parameter parameter;
        String tip;
        if (MethodEnum.COUNT.getValue().equals(methodName)) {
            parameter = new Parameter(new FullyQualifiedJavaType(domainObjectName), poName);
            tip = poName;
        } else {
            parameter = new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), condName);
            tip = condName;
        }
        method.addParameter(parameter);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        StringBuilder sb = new StringBuilder();
        sb.append(getDaoShort());
        sb.append(methodName);
        sb.append("(");
        sb.append(tip);
        sb.append(");");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, condName);
        }
        method.addBodyLine("Assert.notNull(" + tip + ",\"" + tip + "不能为空\");");
        method.addBodyLine("return " + sb.toString());
        return method;
    }


    /**
     * param introspectedTable
     * param type              :返回类型：1,po集合 2 id列表集合
     * return
     */
    protected Method listByCondition(String pluginType, IntrospectedTable introspectedTable, String methodName, int type) {
        if (!this.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        String domainObjectName = introspectedTable.getDomainObjectName();
        String condName = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(domainObjectName));
        String poName = MethodUtils.toLowerCase(domainObjectName);
        Method method = new Method();
        method.setName(methodName);
        String returnType = null;
        Parameter parameter;
        //1:list 2:listId 3:map 4: listByCondition
        if (type == 1 || type == 4) {
            returnType = "List<" + domainObjectName + ">";
        } else if (type == 2) {
            returnType = "List<Integer>";
        } else if (type == 3) {
            returnType = "Map<Integer," + domainObjectName + ">";
        }
        if (type == 1) {
            parameter = new Parameter(new FullyQualifiedJavaType(domainObjectName), poName);
        } else {
            parameter = new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), condName);
        }
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        method.addParameter(parameter);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        return method;
    }


    /**
     * 批量查询列表
     * param introspectedTable
     * param type
     * return
     */
    protected Method batchList(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String methodName) {
        String domainObjectName = introspectedTable.getDomainObjectName();
        String condName = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(domainObjectName));
        Method method = new Method();
        method.setName(methodName);
        String returnType = "List<" + domainObjectName + ">";
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), CommonConstant.GT_ID));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), condName));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@Override");
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, new String[]{CommonConstant.GT_ID, condName});
        }
        method.addBodyLine("Assert.notNull(" + condName + ",\"" + condName + "不能为空\");");
        StringBuilder sb = new StringBuilder();
        sb.append(getDaoShort());
        sb.append(methodName);
        sb.append("(");
        sb.append(CommonConstant.GT_ID).append(", ");
        sb.append(condName);
        sb.append(");");
        method.addBodyLine("return " + sb.toString());
        return method;
    }


    /**
     * add method
     */
    protected Method listByIds(String pluginType, IntrospectedTable introspectedTable, String methodName, int type) {
        if (!this.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        Method method = new Method();
        method.setName(methodName);
        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("List");
        String domainObjectName = introspectedTable.getDomainObjectName();
        if (type == 1) {
            method.setReturnType(new FullyQualifiedJavaType("List<" + domainObjectName + ">"));
        } else {
            method.setReturnType(new FullyQualifiedJavaType("Map<Integer," + domainObjectName + ">"));
        }
        paramType.addTypeArgument(new FullyQualifiedJavaType("java.lang.Integer"));
        method.addParameter(new Parameter(paramType, "ids"));
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        return method;
    }


    /**
     * add method
     */
    protected Method getOtherInteger(String pluginType, String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
        if (!this.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        Method method = new Method();
        method.setName(methodName);
        String params = addParams(introspectedTable, method, type);
        String domainObjectName = introspectedTable.getDomainObjectName();
        method.setVisibility(JavaVisibility.PUBLIC);
        if (MethodEnum.SAVE_AND_GET.getValue().equals(methodName)) {
            method.addAnnotation("@Transactional");
            method.addAnnotation("@Override");
            method.setReturnType(new FullyQualifiedJavaType(domainObjectName));
        } else {
            method.addAnnotation("@Override");
            method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        }
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        StringBuilder sb = new StringBuilder();
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, params);
        }
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(methodName);


        sb.append("(");
        sb.append(params);
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * add method
     */
    protected Method getOtherList(String pluginType, String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
        if (!this.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        Method method = new Method();
        String domainObjectName = introspectedTable.getDomainObjectName();
        String returnType = "List<" + domainObjectName + ">";
        method.setName(methodName);
        method.addAnnotation("@Override");
        String idsStr = "ids";
        if (type == 7) {
            method.setReturnType(new FullyQualifiedJavaType("List<Integer>"));
        } else {
            method.setReturnType(new FullyQualifiedJavaType(returnType));
        }
        String params = addParams(introspectedTable, method, type);
        String condName = MethodGeneratorUtils.getCondName(domainObjectName);
        String lowCond = MethodUtils.toLowerCase(condName);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        if (type == 7) {
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        } else if (type == 6) {
            params = idsStr;
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        } else if (type == 5) {
            params = MethodUtils.toLowerCase(domainObjectName);
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        } else if (type == 8) {
            params = lowCond;
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        }
        String checkStr = "Assert.notNull(" + params + ",\"" + params + "不能为空\");";
        method.addBodyLine(checkStr);
        method.addBodyLine("return " + getDaoShort() + methodName + "(" + params + ");");
        return method;
    }


    /**
     * add method
     */
    protected Method getOtherMap(String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
        if (!this.isCustomEnable(this.className, MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        Method method = new Method();
        String domainObjectName = introspectedTable.getDomainObjectName();
        method.setName(methodName);
        method.addAnnotation("@Override");
        String returnType = "Map<Integer," + domainObjectName + ">";
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        String params = addParams(introspectedTable, method, type);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, params);
        }
        method.addBodyLine("return " + getDaoShort() + methodName + "(" + params + ");");
        return method;
    }


    /**
     * type: pojo 1 key 2 example 3 pojo+example 4
     */
    protected String addParams(IntrospectedTable introspectedTable, Method method, int type1) {
        String domainObjectName = introspectedTable.getDomainObjectName();
        String lowCond = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(domainObjectName));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), lowCond);
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
        Parameter parameter2 = new Parameter(new FullyQualifiedJavaType(domainObjectName), lowPo);
        switch (type1) {
            case 1:
                method.addParameter(new Parameter(pojoType, lowPo)); //$NON-NLS-1$
                return lowPo;
            case 2:
                if (introspectedTable.getRules().generatePrimaryKeyClass()) {
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
                    method.addParameter(new Parameter(type, "key"));
                } else {
                    for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                        FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                        method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
                    }
                }
                StringBuffer sb = new StringBuffer();
                for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append(",");
                }
                sb.setLength(sb.length() - 1);
                return sb.toString();
            case 5:
                method.addParameter(parameter2);
                return lowPo;
            case 6:
                //设置参数类型是List
                FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("List<Integer>");
                method.addParameter(new Parameter(paramType, "ids")); //$NON-NLS-1$
                return "ids";
            case 7:
                method.addParameter(parameter);
                return lowCond;
            case 8:
                method.addParameter(parameter);
                return lowCond;
            default:
                break;
        }
        return null;
    }


    /**
     * import must class
     */
    private void addImport(Interface interfaces, TopLevelClass topLevelClass) {
        if (interfaces != null) {
            interfaces.addImportedType(pojoType);
            interfaces.addImportedType(listType);
            FullyQualifiedJavaType mapType = new FullyQualifiedJavaType("java.util.Map");
            interfaces.addImportedType(mapType);
        }
        if (topLevelClass != null) {
            topLevelClass.addImportedType(daoType);
            topLevelClass.addImportedType(interfaceType);
            topLevelClass.addImportedType(pojoType);
            topLevelClass.addImportedType(listType);
            if (enableLogger) {
                topLevelClass.addImportedType(slf4jLogger);
                topLevelClass.addImportedType(slf4jLoggerFactory);
            }
            if (enableAnnotation) {
                topLevelClass.addImportedType(service);
                topLevelClass.addImportedType(autowired);
            }

            if (topLevelClass.getType().getShortName().endsWith("Impl")) {
                FullyQualifiedJavaType override = new FullyQualifiedJavaType("java.lang.Override");
                topLevelClass.addImportedType(override);
            }
        }
    }


    private String getDaoShort() {
        return MethodUtils.toLowerCase(daoType.getShortName()) + ".";
    }

    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        returnType = method.getReturnType();
        return true;
    }
}
