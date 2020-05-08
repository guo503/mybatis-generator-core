package org.mybatis.generator.utils;

import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;
import org.mybatis.generator.constant.MethodEnum;

/**
 * 基础插件工具类
 * <p>
 * author: guos
 * date: 2019/7/15$ 16:52$
 **/
public class BasePluginUtils {


    /**
     * 添加xml元素
     * param： elementName:元素名字
     * author： guos
     * date 2019/7/15 17:07
     * return
     **/
    public static AbstractXmlElementGenerator addXmlElement(String elementName) {
        AbstractXmlElementGenerator elementGenerator = null;
        if (MethodEnum.RESULT_MAP.getName().equals(elementName)) {
            elementGenerator = new ResultMapWithoutBLOBsElementGenerator(false);
        }
        if (MethodEnum.BASE_COLUMN_LIST.getName().equals(elementName)) {
            elementGenerator = new BaseColumnListElementGenerator();
        } else if (MethodEnum.SQL_CONDITION.getName().equals(elementName)) {
            elementGenerator = new SqlConditionElementGenerator(false);
        } else if (MethodEnum.GET.getName().equals(elementName)) {
            elementGenerator = new SelectByPrimaryKeyElementGenerator();
        } else if (MethodEnum.SAVE.getName().equals(elementName)) {
            elementGenerator = new InsertSelectiveElementGenerator();
        }  else if (MethodEnum.UPDATE.getName().equals(elementName)) {
            elementGenerator = new UpdateByPrimaryKeySelectiveElementGenerator();
        } else if (MethodEnum.LIST_BY_IDS.getName().equals(elementName)) {
            elementGenerator = new ListByIdsElementGenerator();
        }
        return elementGenerator;
    }


    /**
     * 添加java元素
     * param： elementName:元素名字
     * author： guos
     * date 2019/7/15 17:07
     * return
     **/
    public static AbstractJavaMapperMethodGenerator addJavaElement(String elementName) {
        AbstractJavaMapperMethodGenerator javaMapperMethodGenerator = null;
        if (MethodEnum.GET.getName().equals(elementName)) {
            javaMapperMethodGenerator = new SelectByPrimaryKeyMethodGenerator(false);
        } else if (MethodEnum.SAVE.getName().equals(elementName)) {
            javaMapperMethodGenerator = new InsertSelectiveMethodGenerator();
        } else if (MethodEnum.UPDATE.getName().equals(elementName)) {
            javaMapperMethodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
        } else if (MethodEnum.LIST_BY_IDS.getName().equals(elementName)) {
            javaMapperMethodGenerator = new ListByIdsMethodGenerator(false);
        }
        return javaMapperMethodGenerator;
    }


}
