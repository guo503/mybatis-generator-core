package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.KeyConst;
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
public class ManagePlugin extends BasePlugin {

    private FullyQualifiedJavaType manageType;
    private FullyQualifiedJavaType daoType;
    private FullyQualifiedJavaType interfaceType;
    private String managePack;
    private String manageImplPack;
    private String manageProject;
    private String manageImplProject;


    private String mapByIds;
    private String map;

    private String listId;

    private String saveAndGet;

    /**
     * 获取用户名方法
     **/
    private String userNameMethod = null;

    /**
     * 日期格式方法
     **/
    private String dateMethod = null;

    /**
     * 创建时间
     **/
    private String createTime;

    /**
     * 修改时间
     **/
    private String updateTime;

    /**
     * 自定义异常类全路径
     **/
    private String exceptionPack;

    /**
     * 表的列list
     **/
    private List<IntrospectedColumn> columns;

    private String manageSuffix;


    public ManagePlugin() {
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

        page = context.getProperty("page");

        String enableAnnotationStr = context.getProp(className, "enableAnnotation");
        if (StringUtility.stringHasValue(enableAnnotationStr)) {
            enableAnnotation = StringUtility.isTrue(enableAnnotationStr);
        }

        this.manageSuffix = context.getProp(className, "manageSuffix");
        this.map = context.getProp(className, MethodEnum.MAP.getName());
        this.mapByIds = context.getProp(className, MethodEnum.MAP_BY_IDS.getName());
        this.listId = context.getProp(className, MethodEnum.LIST_ID.getName());
        this.saveAndGet = context.getProp(className, MethodEnum.SAVE_AND_GET.getName());

        this.managePack = context.getPPVal(className, "managePack");
        this.manageImplPack = context.getPPVal(className, "manageImplPack");
        this.manageProject = context.getPPVal(className, "manageProject");
        this.manageImplProject = context.getPPVal(className, "manageImplProject");

        this.userNameMethod = context.getProp(className, "userNameMethod");
        this.dateMethod = context.getProp(className, "dateMethod");
        this.createTime = context.getProp(className, "create_time");
        this.updateTime = context.getProp(className, "update_time");
        this.exceptionPack = context.getProp(className, "exceptionPack");
        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) throws IOException {
        String domainObjectName = introspectedTable.getDomainObjectName();
        //是否生成business
        boolean generatorManage = StringUtility.isTrue(context.getTableProp(domainObjectName, KeyConst.ENABLE_MANAGE));
        if (!generatorManage) {//是否生成service
            return new ArrayList<>();
        }
        columns = introspectedTable.getNonPrimaryKeyColumns();

        // 取Service名称【com.coolead.service.PetService】
        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(this.pojoUrl + ".", "");
        String managePath = managePack + "." + tableName + manageSuffix;
        String manageImplPath = manageImplPack + "." + tableName + manageSuffix + "Impl";

        interfaceType = new FullyQualifiedJavaType(managePath);

        // 【com.coolead.mapper.UserMapper】
        daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        // 【com.coolead.service.impl.PetServiceImpl】logger.info(toLowerCase(daoType.getShortName()));
        manageType = new FullyQualifiedJavaType(manageImplPath);

        // 【com.coolead.domain.Pet】
        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        //查询条件类
        String conditionType = context.getProp(ExtendModelPlugin.class.getName(), CommonConstant.CONDITION);

        //分页查询条件类
        String limitConditionType = context.getProp(ExtendModelPlugin.class.getName(), CommonConstant.LIMIT_CONDITION);

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;
        String manageFilePath = manageProject + LocalFileUtils.getPath(managePath) + suffix;
        String manageImplFilePath = manageImplProject + LocalFileUtils.getPath(manageImplPath) + suffix;

        List<GeneratedJavaFile> manageFiles = new ArrayList<>();
        List<GeneratedJavaFile> manageImplFiles = new ArrayList<>();

        Interface interface1 = new Interface(interfaceType);
        interface1.addImportedType(new FullyQualifiedJavaType(conditionType));
        interface1.addImportedType(new FullyQualifiedJavaType(limitConditionType));
        TopLevelClass topLevelClass = new TopLevelClass(manageType);
        topLevelClass.addImportedType(new FullyQualifiedJavaType(conditionType));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(limitConditionType));
        //先删除
        Files.deleteIfExists(Paths.get(manageFilePath));
        // 导入必须的类
        addImport(interface1, null);
        interface1.addImportedType(MethodGeneratorUtils.getPoType(context, introspectedTable));
        // 接口
        addService(interface1, introspectedTable, tableName, manageFiles);
        //添加接口注释
        CommentUtils.addGeneralInterfaceComment(interface1, introspectedTable);
        List<GeneratedJavaFile> files = new ArrayList<>(manageFiles);
        //先删除
        Files.deleteIfExists(Paths.get(manageImplFilePath));

        // 导入必须的类
        addImport(null, topLevelClass);
        //添加类注释
        CommentUtils.addGeneralClassComment(topLevelClass, introspectedTable);

        if (this.hasDateColumn(dateMethod, createTime, updateTime)) {//是否需要导入date类
            FullyQualifiedJavaTypeUtils.importType(null, topLevelClass, "java.util.Date");
        }

        if (StringUtility.stringHasValue(exceptionPack)) {
            FullyQualifiedJavaTypeUtils.importType(null, topLevelClass, exceptionPack);
        }
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
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, manageProject, fileEncoding, context.getJavaFormatter());

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
        topLevelClass.addImportedType(manageType);
        ;
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
        addField(topLevelClass);

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
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, manageImplProject, fileEncoding, context.getJavaFormatter());
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
     * 添加方法
     * flag 1:根据id查询
     */
    protected Method selectByPrimaryKey(IntrospectedTable introspectedTable, String alias, String tableName) {
        if (!context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(alias))) {
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
        sb.append(alias).append("x");
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
        sb.append(alias).append("x");
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
        if (!context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(alias))) {
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
        sb.append(alias).append("x");
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
        if (!context.isCustomEnable(BaseMethodPlugin.class.getName(), MethodEnum.getNameByValue(methodName))) {
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
        sb.append(methodName).append("x");
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
        if (!context.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        String domainObjectName = introspectedTable.getDomainObjectName();
        String condName = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(domainObjectName));
        String poName = MethodUtils.toLowerCase(domainObjectName);
        Method method = new Method();
        method.setName(methodName);
        String returnType = null;
        Parameter parameter;
        String tip;
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
        //method.addParameter(new Parameter(this.getShortPojoType(introspectedTable), condName));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), condName));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@Override");
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        StringBuilder sb = new StringBuilder();
        sb.append(getDaoShort());
        sb.append(methodName);
        sb.append("(");
        sb.append(condName);
        sb.append(");");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, new String[]{CommonConstant.GT_ID, condName});
        }
        method.addBodyLine("Assert.notNull(" + condName + ",\"" + condName + "不能为空\");");
        if (StringUtility.stringHasValue(page)) {
            if (topLevelClass != null) {
                topLevelClass.addImportedType(page);
            }
            method.addBodyLine(condName + ".limit(1," + MethodUtils.getClassName(page) + ".getMaxRow() - 1);");
            method.addBodyLine(condName + ".setOrderBy(" + domainObjectName + ".ID);");
            method.addBodyLine(condName + ".andCriteria().andGreaterThan(" + domainObjectName + ".ID, gtId);");
        }
        method.addBodyLine("return this.listByCondition(" + condName + ");");
        return method;
    }


    /**
     * add method
     */
    protected Method listByIds(String pluginType, IntrospectedTable introspectedTable, String methodName, int type) {
        if (!context.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
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
        if (!context.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
            return null;
        }
        Method method = new Method();
        method.setName(methodName);
        String params = addParams(introspectedTable, method, type);
        String domainObjectName = introspectedTable.getDomainObjectName();
        String domainName = MethodUtils.toLowerCase(domainObjectName);
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

        if (MethodEnum.SAVE.getValue().equals(methodName) || MethodEnum.UPDATE.getValue().equals(methodName)) {
            String getUserName;
            String getDate;
            method.addBodyLine("Assert.notNull(" + domainName + "," + "\"" + domainName + "不能为空\");");
            if (MethodEnum.SAVE.getValue().equals(methodName)) {
                String creator = context.getProp(pluginType, "creator");
                getUserName = StringUtility.stringHasValue(creator) ? creator : CommonConstant.DEFAULT_USER;
                getDate = this.getMethodName(dateMethod, createTime, CommonConstant.DEFAULT_TIME);

                this.setMethodValue(method, params, creator, getUserName);//设置用户名
                this.setMethodValue(method, params, createTime, getDate);//设置时间
            } else {
                String updater = context.getProp(pluginType, "updater");
                getUserName = StringUtility.stringHasValue(updater) ? updater : CommonConstant.DEFAULT_USER;
                getDate = this.getMethodName(dateMethod, updateTime, CommonConstant.DEFAULT_TIME);
                this.setMethodValue(method, params, updater, getUserName);//设置用户名
                this.setMethodValue(method, params, updateTime, getDate);//设置时间
            }
        }
        //saveAndGet
        if (MethodEnum.SAVE_AND_GET.getValue().equals(methodName)) {
            method.addBodyLine("this." + MethodEnum.SAVE.getValue() + "(" + params + ");");
            method.addBodyLine("return this." + MethodEnum.GET.getValue() + "(" + params + ".getId());");
            return method;
        }
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(methodName);
        sb.append("x");


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
        if (!context.isCustomEnable(pluginType, MethodEnum.getNameByValue(methodName))) {
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
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
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
        if (type == 7) {//listId
            method.addBodyLine(returnType + " list = this.listByCondition(" + lowCond + ");");
            String getKey = "get" + MethodUtils.toUpperCase(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
            method.addBodyLine("return list.stream().map(" + domainObjectName + "::" + getKey + ").distinct().collect(Collectors.toList());");
        } else if (type == 6) {//listByIds
            method.addBodyLine("if (CollectionUtils.isEmpty(ids)) {");
            method.addBodyLine("return Lists.newArrayList();");
            method.addBodyLine("}");
            method.addBodyLine("Condition<" + domainObjectName + "> " + lowCond + " = new Condition<>();");
            method.addBodyLine(lowCond + ".createCriteria().andIn(" + domainObjectName + ".ID, ids);");
            if (StringUtility.stringHasValue(page)) {
                method.addBodyLine(lowCond + ".limit(Page.getMaxRow());");
            }
            method.addBodyLine("return this.listByCondition(" + lowCond + ");");
        } else if (type == 5) {//list
            String resStr = getDaoShort() + "listLimitx(" + params + ", new LimitCondition(" + lowPo + ".getStart(), " + lowPo + ".getRow()));";
            method.addBodyLine(checkStr);
            method.addBodyLine("return " + resStr);
        } else if (type == 8) {//listByCondition
            String resStr = getDaoShort() + "listByConditionx(" + params + ");";
            method.addBodyLine(checkStr);
            method.addBodyLine("return " + resStr);
        }
        return method;
    }


    /**
     * add method
     */
    protected Method getOtherMap(String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
        if (!context.isCustomEnable(this.className, MethodEnum.getNameByValue(methodName))) {
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
        String invokeMethod;
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
        String lowPoList = lowPo + "List";
        if (MethodEnum.MAP.getValue().equals(methodName) || MethodEnum.MAP_BY_CONDITION.getValue().equals(methodName)) {
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
            invokeMethod = listByCondition;
            method.addBodyLine("List<" + domainObjectName + "> " + lowPoList + " = this." + invokeMethod + "(" + params + ");");
        } else {
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, "ids");
            }
            invokeMethod = listByIds;
            method.addBodyLine("List<" + domainObjectName + "> " + lowPoList + " = this." + invokeMethod + "(ids);");
        }
        String getKey = "get" + MethodUtils.toUpperCase(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
        method.addBodyLine("return " + lowPoList + ".stream().collect(Collectors.toMap(" + domainObjectName + "::" + getKey + "," + lowPo + " -> " + lowPo + ", (k1, k2) -> k2));");

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
                if (StringUtility.stringHasValue(userNameMethod)) {
                    FullyQualifiedJavaTypeUtils.importType(null, topLevelClass, MethodUtils.getFullClass(userNameMethod, "."));
                }

                if (StringUtility.stringHasValue(dateMethod)) {
                    FullyQualifiedJavaTypeUtils.importType(interfaces, topLevelClass, MethodUtils.getFullClass(dateMethod, "."));
                }

            }
        }
    }


    private String getDaoShort() {
        return MethodUtils.toLowerCase(daoType.getShortName()) + ".";
    }

    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType returnType = method.getReturnType();
        return true;
    }


    private String getMethodName(String fullMethodName, String str, String defaultValue) {
        String setValue = null;
        if (!hasColumn(str)) {
            return null;
        }
        if (StringUtility.stringHasValue(fullMethodName)) {
            setValue = MethodUtils.getFullMethod(fullMethodName, ".");
        }
        if (!StringUtility.stringHasValue(setValue)) {//没有配置默认方法，则使用默认值
            setValue = defaultValue;
        }
        return setValue;
    }


    private boolean hasColumn(String str) {
        if (!StringUtility.stringHasValue(str)) {
            return false;
        }
        for (IntrospectedColumn column : columns) {
            if (str.equals(column.getActualColumnName())) {
                return true;
            }
        }
        return false;
    }

    //设置方法值
    private void setMethodValue(Method method, String params, String column, String val) {
        if (!hasColumn(column)) {
            return;
        }
        method.addBodyLine("if (StringUtils.isEmpty(" + MethodUtils.generateGet(params, column) + ")) {");
        if (CommonConstant.DEFAULT_USER.equals(val)) {
            method.addBodyLine(MethodUtils.generateSet(params, column, "\"" + val + "\"") + ";");
        } else {
            method.addBodyLine(MethodUtils.generateSet(params, column, val) + ";");
        }
        method.addBodyLine("}");
    }

    private boolean hasDateColumn(String dateMethod, String... actualColumns) {
        if (!StringUtility.stringHasValue(dateMethod)) {
            if (actualColumns != null && actualColumns.length > 0) {
                for (String column : actualColumns) {
                    if (hasColumn(column)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
