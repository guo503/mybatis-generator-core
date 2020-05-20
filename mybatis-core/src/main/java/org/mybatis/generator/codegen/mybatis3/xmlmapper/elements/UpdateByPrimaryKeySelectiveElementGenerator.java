/**
 * Copyright 2006-2017 the original author or authors.
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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.utils.MapperXmlUtils;

import java.util.List;

/**
 * @author Jeff Butler
 */
public class UpdateByPrimaryKeySelectiveElementGenerator extends
        AbstractXmlElementGenerator {

    public UpdateByPrimaryKeySelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getUpdateByPrimaryKeySelectiveStatementId())); //$NON-NLS-1$

        String parameterType;

        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = introspectedTable.getRecordWithBLOBsType();
        } else {
            parameterType = introspectedTable.getBaseRecordType();
        }

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterType));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();

        sb.append("update "); //$NON-NLS-1$
        sb.append(MapperXmlUtils.addQuotation(introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
        answer.addElement(dynamicElement);
        List<IntrospectedColumn> columns = ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getNonPrimaryKeyColumns());
        String versionCol = null;
        List<TableConfiguration> tableConfigurationList = context.getTableConfigurations();
        //是否生成乐观锁
        for (TableConfiguration tableConfiguration : tableConfigurationList) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                versionCol = context.getTableProp(introspectedTable.getDomainObjectName(),"versionCol");
            }
        }
        boolean hasVersions = false;
        for (IntrospectedColumn introspectedColumn : columns) {
            if (StringUtility.stringHasValue(versionCol) && versionCol.equals(introspectedColumn.getActualColumnName())) {
                TextElement versionsElement = new TextElement(MapperXmlUtils.addQuotation(versionCol) + "=" + MapperXmlUtils.addQuotation(versionCol) + "+1,"); //$NON-NLS-1$
                dynamicElement.addElement(versionsElement);
                hasVersions = true;
                break;
            }
        }
        for (IntrospectedColumn introspectedColumn : columns) {
            if (StringUtility.stringHasValue(versionCol) && versionCol.equals(introspectedColumn.getActualColumnName())) {
                continue;
            }
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MapperXmlUtils.addQuotation(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn)));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            sb.append(',');
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and "); //$NON-NLS-1$
            } else {
                sb.append("where "); //$NON-NLS-1$
                and = true;
            }

            sb.append(MapperXmlUtils.addQuotation(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn)));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            answer.addElement(new TextElement(sb.toString()));


            //添加乐观锁判断
            if (hasVersions) {
                sb.setLength(0);
                sb.append(versionCol);
                sb.append(" != null"); //$NON-NLS-1$
                XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                isNotNullElement.addElement(new TextElement(" and " + MapperXmlUtils.addQuotation(versionCol) + "=#{" + versionCol + ",jdbcType=INTEGER}"));
                answer.addElement(isNotNullElement);
            }
        }

        if (context.getPlugins()
                .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
