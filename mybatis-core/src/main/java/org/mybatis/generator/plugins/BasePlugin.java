package org.mybatis.generator.plugins;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.constant.MethodEnum;

import java.util.List;
import java.util.Properties;

/**
 * 基础公共业务代码
 * @author guos
 * description
 * date 2020/5/21 16:00
 **/
public class BasePlugin extends PluginAdapter {

    protected FullyQualifiedJavaType slf4jLogger;

    protected  FullyQualifiedJavaType slf4jLoggerFactory;

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

    /**
     * 是否生成logger日志
     */
    protected boolean enableLogger;


    public BasePlugin() {
        super();
        // default is slf4j
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
        listType = new FullyQualifiedJavaType("java.util.*");
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

        autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
        service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
    }
}
