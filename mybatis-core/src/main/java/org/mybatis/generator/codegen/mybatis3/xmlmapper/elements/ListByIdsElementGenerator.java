/**
 *    Copyright 2006-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.utils.MapperXmlUtils;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class ListByIdsElementGenerator extends
        AbstractXmlElementGenerator {

    public ListByIdsElementGenerator() {
        super();
    }

    private boolean isSimple;

    public ListByIdsElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        //先创建一个select标签
        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$
        //设置该select标签的id，在枚举中设置的值
        answer.addAttribute(new Attribute("id", introspectedTable.getListByIdsStatementId()));

        //设置resultMap为BaseResultMap
        answer.addAttribute(new Attribute("resultMap",introspectedTable.getBaseResultMapId()));

        context.getCommentGenerator().addComment(answer);
        //接下去是拼接我们的sql
        StringBuilder sb = new StringBuilder();
        sb.append("select "); //$NON-NLS-1$

        if (stringHasValue(introspectedTable
                .getSelectByPrimaryKeyQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
            sb.append("' as QUERYID,"); //$NON-NLS-1$
        }
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getBaseColumnListElement());
        if (introspectedTable.hasBLOBColumns()) {
            answer.addElement(new TextElement(",")); //$NON-NLS-1$
            answer.addElement(getBlobColumnListElement());
        }

        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        sb.append(MapperXmlUtils.addQuotation(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime()));
        answer.addElement(new TextElement(sb.toString()));
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
                    .getAliasedEscapedColumnName(introspectedColumn)));
            sb.append(" in "); //$NON-NLS-1$
            answer.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            XmlElement foreachElement = new XmlElement("foreach");
            foreachElement.addAttribute(new Attribute("item", "item"));
            foreachElement.addAttribute(new Attribute("index", "index"));
            foreachElement.addAttribute(new Attribute("open", "("));
            foreachElement.addAttribute(new Attribute("separator", ","));
            foreachElement.addAttribute(new Attribute("close", ")"));
            foreachElement.addAttribute(new Attribute("collection", "list"));
            foreachElement.addElement(new TextElement("#{item}"));
            answer.addElement(foreachElement);

        }

        if (context.getPlugins().sqlMapSelectAllElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

}
