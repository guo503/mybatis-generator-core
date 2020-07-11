package org.mybatis.generator.plugins;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.constant.MethodEnum;

import java.util.List;
import java.util.Properties;

/**
 * 基础公共业务代码
 *
 * @author guos
 * description
 * date 2020/5/21 16:00
 **/
public class BasePlugin extends PluginAdapter {

    protected FullyQualifiedJavaType slf4jLogger;

    protected FullyQualifiedJavaType slf4jLoggerFactory;

    protected FullyQualifiedJavaType autowired;

    protected FullyQualifiedJavaType service;

    protected FullyQualifiedJavaType listType;

    protected FullyQualifiedJavaType pojoType;

    protected String fileEncoding;

    protected boolean enableAnnotation;

    protected String pojoUrl;

    protected String deleteByCondition;
    protected String insertSelective;
    protected String updateByPrimaryKeySelective;
    protected String selectByPrimaryKey;
    protected String listByIds;
    protected String countByCondition;
    protected String listByCondition;
    protected String count;
    protected String list;
    protected String mapByIds;
    protected String map;
    protected String listId;
    protected String saveAndGet;


    protected FullyQualifiedJavaType iManage;
    protected FullyQualifiedJavaType manageImpl;

    protected FullyQualifiedJavaType iService;
    protected FullyQualifiedJavaType serviceImpl;

    /**
     * 是否生成logger日志
     */
    protected boolean enableLogger;

    /**
     * 类名
     */
    protected String className;

    /**
     * 分页类路径
     */
    protected String page;

    /**
     * 自定义异常类全路径
     **/
    protected String exceptionPack;


    public BasePlugin() {
        super();
        className = this.getClass().getName();
        slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.pojoUrl = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        this.listType = new FullyQualifiedJavaType("java.util.*");
        String daoType = BaseMethodPlugin.class.getName();
        this.selectByPrimaryKey = context.getProp(daoType, MethodEnum.GET.getName());
        this.insertSelective = context.getProp(daoType, MethodEnum.SAVE.getName());
        this.updateByPrimaryKeySelective = context.getProp(daoType, MethodEnum.UPDATE.getName());
        this.listByIds = context.getProp(daoType, MethodEnum.LIST_BY_IDS.getName());
        this.listByCondition = context.getProp(daoType, MethodEnum.LIST_BY_CONDITION.getName());
        this.countByCondition = context.getProp(daoType, MethodEnum.COUNT_BY_CONDITION.getName());
        this.count = context.getProp(daoType, MethodEnum.COUNT.getName());
        this.list = context.getProp(daoType, MethodEnum.LIST.getName());
        this.deleteByCondition = context.getProp(daoType, MethodEnum.DELETE_BY_CONDITION.getName());
        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);
        this.map = context.getProp(daoType, MethodEnum.MAP.getName());
        this.mapByIds = context.getProp(daoType, MethodEnum.MAP_BY_IDS.getName());
        this.listId = context.getProp(daoType, MethodEnum.LIST_ID.getName());
        this.saveAndGet = context.getProp(daoType, MethodEnum.SAVE_AND_GET.getName());

        this.page = "mybatis.core.page.Page";

        this.iManage = new FullyQualifiedJavaType("mybatis.base.template.manage.IManage");
        this.manageImpl = new FullyQualifiedJavaType("mybatis.base.template.manage.ManageImpl");

        this.iService = new FullyQualifiedJavaType("mybatis.base.template.service.IService");
        this.serviceImpl = new FullyQualifiedJavaType("mybatis.base.template.service.ServiceImpl");

        this.autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
        this.service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
    }
}
