/**
 * Copyright 2006-2015 the original author or authors.
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

import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.utils.MapperXmlUtils;

/**
 *
 * @author Jeff Butler
 *
 */
public class SqlConditionElementGenerator extends
        AbstractXmlElementGenerator {

    public SqlConditionElementGenerator() {
        super();
    }

    private boolean isSimple;

    public SqlConditionElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {

        //生成<sql></sql>
        XmlElement answer = MapperXmlUtils.createSqlCondition(introspectedTable); //$NON-NLS-1$
        if (context.getPlugins().sqlMapSelectAllElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

}
