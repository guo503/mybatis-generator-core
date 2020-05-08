package org.mybatis.generator.utils;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * Description: xml文件生成工具
 * Author: guos
 * Date: 2019/2/22 14:08
 **/
public class MapperXmlUtils {


    /**
     * sql标签名字
     **/
    public static final String SQL_NAME = "cond_selective";


    /**
     * Description:  生成sql片段
     * param introspectedTable
     * Author: guos
     * Date: 2019/2/22 14:24
     * Return:
     **/
    public static XmlElement createSqlCondition(IntrospectedTable introspectedTable) {
        //在这里添加where条件
        XmlElement sqlElement = new XmlElement("sql"); //设置sql标签
        sqlElement.addAttribute(new Attribute("id", SQL_NAME));
        //在这里添加where条件
        XmlElement selectTrimElement = new XmlElement("trim"); //设置trim标签
        selectTrimElement.addAttribute(new Attribute("prefix", "where"));
        selectTrimElement.addAttribute(new Attribute("prefixOverrides", "and")); //添加where和and
        StringBuilder sb = new StringBuilder();


        //gtId
        XmlElement gtIdNotNullElement = new XmlElement("if"); //$NON-NLS-1$
        sb.append("gtId != null");
        gtIdNotNullElement.addAttribute(new Attribute("test", sb.toString()));
        sb.setLength(0);
        sb.append(" and " + MapperXmlUtils.addQuotation("id") + " > #{" + CommonConstant.GT_ID + ",jdbcType=INTEGER}"); //添加等号
        gtIdNotNullElement.addElement(new TextElement(sb.toString()));
        selectTrimElement.addElement(gtIdNotNullElement);
        //循环所有的列
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            sb.setLength(0);
            XmlElement selectNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null ");
            selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            sb.setLength(0);
            sb.append(" and "); //添加and
            sb.append(MapperXmlUtils.addQuotation(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn)));
            sb.append(" = "); //添加等号
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            selectNotNullElement.addElement(new TextElement(sb.toString()));
            selectTrimElement.addElement(selectNotNullElement);
        }
        sqlElement.addElement(selectTrimElement);
        return sqlElement;
    }

    /**
     * Description:  生成include片段
     * param
     * Author: guos
     * Date: 2019/2/22 14:24
     * Return:
     **/
    public static XmlElement createIncludeElement() {
        //在这里引入include
        XmlElement includeElement = new XmlElement("include"); //设置include标签
        includeElement.addAttribute(new Attribute("refid", SQL_NAME));
        return includeElement;
    }


    /**
     * Description:添加单引号
     * param
     * Author: guos
     * Date: 2019/2/22 14:24
     * Return:
     **/
    public static String addQuotation(String str) {
        return "`" + str + "`";
    }

    /**
     * Description:添加双引号
     * param
     * Author: guos
     * Date: 2019/2/22 14:24
     * Return:
     **/
    public static String getSqlId(String str) {
        return "id=\"" + str + "\"";
    }



    public static String getEnableDeleteByPrimaryKey(PluginConfiguration baseMethodPlugin) {
        return baseMethodPlugin.getProperty(MethodEnum.DELETE.getName());
    }

    public static String getEnableInsertSelective(PluginConfiguration baseMethodPlugin) {
        String enableInsertSelective = baseMethodPlugin.getProperty(MethodEnum.SAVE.getName());
        return StringUtility.stringHasValue(enableInsertSelective) ? enableInsertSelective : MethodEnum.SAVE.getValue();
    }

    public static String getEnableSelectByPrimaryKey(PluginConfiguration baseMethodPlugin) {
        String enableSelectByPrimaryKey = baseMethodPlugin.getProperty(MethodEnum.GET.getName());
        return StringUtility.stringHasValue(enableSelectByPrimaryKey) ? enableSelectByPrimaryKey : MethodEnum.GET.getValue();
    }

    public static String getEnableUpdateByPrimaryKeySelective(PluginConfiguration baseMethodPlugin) {
        String enableUpdateByPrimaryKeySelective = baseMethodPlugin.getProperty(MethodEnum.UPDATE.getName());
        return StringUtility.stringHasValue(enableUpdateByPrimaryKeySelective) ? enableUpdateByPrimaryKeySelective : MethodEnum.UPDATE.getValue();
    }

    public static String getListByIds(PluginConfiguration baseMethodPlugin) {
        String listByIds = baseMethodPlugin.getProperty(MethodEnum.LIST_BY_IDS.getName());
        return StringUtility.stringHasValue(listByIds) ? listByIds : MethodEnum.LIST_BY_IDS.getValue();
    }

    public static String getCountByCondition(PluginConfiguration baseMethodPlugin) {
        String countByCondition = baseMethodPlugin.getProperty(MethodEnum.COUNT.getName());
        return StringUtility.stringHasValue(countByCondition) ? countByCondition : MethodEnum.COUNT.getValue();
    }

    public static String getListByCondition(PluginConfiguration baseMethodPlugin) {
        String listByCondition = baseMethodPlugin.getProperty(MethodEnum.LIST.getName());
        return StringUtility.stringHasValue(listByCondition) ? listByCondition : MethodEnum.LIST.getValue();
    }

    public static String getDeleteByCondition(PluginConfiguration baseMethodPlugin) {
        return baseMethodPlugin.getProperty(MethodEnum.DELETE_BY_CONDITION.getName());
    }


}
