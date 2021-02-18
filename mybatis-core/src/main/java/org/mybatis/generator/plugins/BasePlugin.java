package org.mybatis.generator.plugins;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.constant.MpEnum;

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


    protected FullyQualifiedJavaType IBusiness;
    protected FullyQualifiedJavaType businessImpl;

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

    /**
     * query类包路径
     */
    protected String queryPack;

    /**
     * query类后缀名
     */
    protected String querySuffix;

    /**
     * vo包路径
     */
    protected String voPack;

    /**
     * vo类后缀名
     */
    protected String voSuffix;

    /**
     * 项目结构
     */
    protected boolean isBS;


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
        this.voPack = context.getPPVal(ExtendModelPlugin.class.getName(), "voPack");
        this.voSuffix = context.getProp(ExtendModelPlugin.class.getName(), "voSuffix");

        this.iService = new FullyQualifiedJavaType(context.getProp(ExtendModelPlugin.class.getName(), MpEnum.IService.getValue()));
        this.serviceImpl = new FullyQualifiedJavaType(context.getProp(ExtendModelPlugin.class.getName(), MpEnum.ServiceImpl.getValue()));
        this.fileEncoding = context.getProperty("javaFileEncoding");

        String pack = "mybatis.base.template.";
        this.IBusiness = new FullyQualifiedJavaType(pack + "business.IBusiness");
        this.businessImpl = new FullyQualifiedJavaType(pack + "business.BusinessImpl");

        this.autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
        this.service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
    }
}
