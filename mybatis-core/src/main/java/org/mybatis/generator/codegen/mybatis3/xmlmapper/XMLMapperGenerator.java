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
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.plugins.ExtendModelPlugin;
import org.mybatis.generator.utils.BasePluginUtils;
import org.mybatis.generator.utils.LocalFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 */
public class XMLMapperGenerator extends AbstractXmlGenerator {

    public XMLMapperGenerator() {
        super();
    }

    protected XmlElement getSqlMapElement() {
        String generatorXml = context.getProp(ExtendModelPlugin.class.getName(), "generatorXml");
        if (!StringUtility.isTrue(generatorXml)) {
            return null;
        }
        String domainPojoName = introspectedTable.getDomainObjectName();
        String suffix = ".xml";
        //mapper.xml文件路径
        String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();
        String xmlPath = targetProject + LocalFileUtils.getPath(context.getSqlMapGeneratorConfiguration().getTargetPackage()) + File.separator + domainPojoName + "Mapper" + suffix;
        try {
            Files.deleteIfExists(Paths.get(xmlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.12", table.toString())); //$NON-NLS-1$
        XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                namespace));

        context.getCommentGenerator().addRootComment(answer);
        //resultMap
        this.addXmlElement(answer, MethodEnum.RESULT_MAP.getName());
        this.addXmlElement(answer, MethodEnum.BASE_COLUMN_LIST.getName());
        return answer;
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
