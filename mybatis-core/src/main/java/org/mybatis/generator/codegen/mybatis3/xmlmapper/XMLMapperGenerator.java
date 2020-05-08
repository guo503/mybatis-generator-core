/**
 * Copyright 2006-2016 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.utils.BasePluginUtils;
import org.mybatis.generator.utils.ContextUtils;
import org.mybatis.generator.utils.LocalFileUtils;
import org.mybatis.generator.utils.MapperXmlUtils;

import java.io.File;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 */
public class XMLMapperGenerator extends AbstractXmlGenerator {

    /**
     * extentModel插件类
     **/
    private PluginConfiguration extentModelPlugin;

    /**
     * 基础方法插件类
     **/
    private PluginConfiguration baseMethodPlugin;


    public XMLMapperGenerator() {
        super();
    }

    protected XmlElement getSqlMapElement() {

        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.12", table.toString())); //$NON-NLS-1$
        XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                namespace));

        context.getCommentGenerator().addRootComment(answer);

        extentModelPlugin = ContextUtils.getPlugin(context, CommonConstant.EXTEND_MODEL_PLUGIN);

        baseMethodPlugin = ContextUtils.getPlugin(context, CommonConstant.BASE_METHOD_PLUGIN);

        String generatorXml = extentModelPlugin.getProperty("generatorXml");

        String domainPojoName = introspectedTable.getDomainObjectName();
        String suffix = ".xml";
        //mapper.xml文件路径
        String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();
        String xmlPath = targetProject + LocalFileUtils.getPath(context.getSqlMapGeneratorConfiguration().getTargetPackage()) + File.separator + domainPojoName + "Mapper" + suffix;
        File xmlFile = new File(xmlPath);
        boolean xmlFileExist = xmlFile.exists();
        if (StringUtility.isTrue(generatorXml)) {
            //generateElement(answer, xmlPath, xmlFileExist);
            //存在删除旧的生成新的文件
            if (xmlFileExist) {
                xmlFile.delete();
            }
            //resultMap
            this.addXmlElement(answer, MethodEnum.RESULT_MAP.getName());
            //update
            //this.addXmlElement(answer, MethodEnum.UPDATE.getName());
        }
        return answer;
    }

    private void generateElement(XmlElement answer, String xmlPath, boolean xmlFileExist) {
        String deleteByCondition = MapperXmlUtils.getDeleteByCondition(baseMethodPlugin);
        if (xmlFileExist) {//存在根据方法追加
            if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MapperXmlUtils.getEnableSelectByPrimaryKey(baseMethodPlugin)))) {//get方法
                this.addXmlElement(answer, MethodEnum.GET.getName());
            }
            if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MapperXmlUtils.SQL_NAME))) {//listByIds方法
                this.addXmlElement(answer, MethodEnum.SQL_CONDITION.getName());
            }
           /* if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MethodUtils.getSelectPoMethodStr(domainPojoName)))) {//getPojo方法
                addSelectBySelectiveElement(answer);
            }*/
            if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MapperXmlUtils.getEnableInsertSelective(baseMethodPlugin)))) {//save方法
                this.addXmlElement(answer, MethodEnum.SAVE.getName());
            }

            if (StringUtility.stringHasValue(deleteByCondition)) {
                if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(deleteByCondition))) {//删除方法
                    this.addXmlElement(answer, MethodEnum.SQL_CONDITION.getName());
                }
            }

            if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MapperXmlUtils.getEnableUpdateByPrimaryKeySelective(baseMethodPlugin)))) {//get方法
                this.addXmlElement(answer, MethodEnum.UPDATE.getName());
            }

            if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MapperXmlUtils.getListByIds(baseMethodPlugin)))) {//listByIds方法
                this.addXmlElement(answer, MethodEnum.LIST_BY_IDS.getName());
            }
            if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MapperXmlUtils.getListByCondition(baseMethodPlugin)))) {//list方法
                this.addXmlElement(answer, MethodEnum.LIST.getName());
            }
            if (!LocalFileUtils.findStr(xmlPath, MapperXmlUtils.getSqlId(MapperXmlUtils.getCountByCondition(baseMethodPlugin)))) {//count方法
                this.addXmlElement(answer, MethodEnum.COUNT.getName());
            }
        } else {//不存在根据全部追加
            this.addXmlElement(answer, MethodEnum.RESULT_MAP.getName());
            this.addXmlElement(answer, MethodEnum.BASE_COLUMN_LIST.getName());
            this.addXmlElement(answer, MethodEnum.GET.getName());
            this.addXmlElement(answer, MethodEnum.SAVE.getName());
            if (StringUtility.stringHasValue(deleteByCondition)) {
                this.addXmlElement(answer, MethodEnum.DELETE_BY_CONDITION.getName());
            }
            this.addXmlElement(answer, MethodEnum.UPDATE.getName());
            this.addXmlElement(answer, MethodEnum.LIST_BY_IDS.getName());
            this.addXmlElement(answer, MethodEnum.SQL_CONDITION.getName());
            this.addXmlElement(answer, MethodEnum.COUNT.getName());
            this.addXmlElement(answer, MethodEnum.LIST.getName());


            //addResultMapWithoutBLOBsElement(answer);
            //addBaseColumnListElement(answer);
            //addSelectByPrimaryKeyElement(answer);
            //addInsertSelectiveElement(answer);
            //addUpdateByPrimaryKeySelectiveElement(answer);
            //addListByIdsElement(answer);
            //addSqlConditionElement(answer);
            //addListByConditionElement(answer);
            //addCountByConditionElement(answer);
        }
    }


    /**
     * 添加xml元素
     * param： elementName:元素名字
     * author： guos
     * date 2019/7/15 17:07
     * return
     **/
    private void addXmlElement(XmlElement parentElement, String elementName) {
        AbstractXmlElementGenerator elementGenerator = BasePluginUtils.addXmlElement(elementName);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }


    protected void addResultMapWithoutBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBaseResultMap()) {
            AbstractXmlElementGenerator elementGenerator = new ResultMapWithoutBLOBsElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addResultMapWithBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new ResultMapWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addExampleWhereClauseElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSQLExampleWhereClause()) {
            AbstractXmlElementGenerator elementGenerator = new ExampleWhereClauseElementGenerator(
                    false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addMyBatis3UpdateByExampleWhereClauseElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules()
                .generateMyBatis3UpdateByExampleWhereClause()) {
            AbstractXmlElementGenerator elementGenerator = new ExampleWhereClauseElementGenerator(
                    true);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addBaseColumnListElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBaseColumnList()) {
            AbstractXmlElementGenerator elementGenerator = new BaseColumnListElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addBlobColumnListElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBlobColumnList()) {
            AbstractXmlElementGenerator elementGenerator = new BlobColumnListElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectByExampleWithoutBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithoutBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectByExampleWithBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectByPrimaryKeyElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractXmlElementGenerator elementGenerator = new SelectByPrimaryKeyElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectBySelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractXmlElementGenerator elementGenerator = new SelectBySelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addDeleteByExampleElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateDeleteByExample()) {
            AbstractXmlElementGenerator elementGenerator = new DeleteByExampleElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addDeleteByPrimaryKeyElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractXmlElementGenerator elementGenerator = new DeleteByPrimaryKeyElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addInsertElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractXmlElementGenerator elementGenerator = new InsertElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addInsertSelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractXmlElementGenerator elementGenerator = new InsertSelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addCountByExampleElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateCountByExample()) {
            AbstractXmlElementGenerator elementGenerator = new CountByExampleElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByExampleSelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByExampleSelective()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByExampleSelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByExampleWithBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByExampleWithoutBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithoutBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByPrimaryKeySelectiveElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeySelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByPrimaryKeyWithBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByPrimaryKeyWithoutBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addListByIdsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new ListByIdsElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSqlConditionElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new SqlConditionElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }


    protected void initializeAndExecuteGenerator(
            AbstractXmlElementGenerator elementGenerator,
            XmlElement parentElement) {
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.setProgressCallback(progressCallback);
        elementGenerator.setWarnings(warnings);
        elementGenerator.addElements(parentElement);
    }

    @Override
    public Document getDocument() {
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        XmlElement xmlElement = getSqlMapElement();
        if (xmlElement == null) {
            return null;
        }
        document.setRootElement(xmlElement);
        if (!context.getPlugins().sqlMapDocumentGenerated(document,
                introspectedTable)) {
            document = null;
        }

        return document;
    }


}
