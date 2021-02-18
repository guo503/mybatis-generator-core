/**
 *    Copyright 2006-2017 the original author or authors.
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
package org.mybatis.generator.codegen.mybatis3.javamapper;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

public class AnnotatedClientGenerator extends JavaMapperGenerator {

    public AnnotatedClientGenerator() {
        super(false);
    }

    @Override
    public List<CompilationUnit> getExtraCompilationUnits() {
        boolean useLegacyBuilder = false;

        String prop = context.getJavaClientGeneratorConfiguration()
                .getProperty(PropertyRegistry.CLIENT_USE_LEGACY_BUILDER);
        if (StringUtility.stringHasValue(prop)) {
            useLegacyBuilder = Boolean.valueOf(prop);
        }
        SqlProviderGenerator sqlProviderGenerator = new SqlProviderGenerator(useLegacyBuilder);
        sqlProviderGenerator.setContext(context);
        sqlProviderGenerator.setIntrospectedTable(introspectedTable);
        sqlProviderGenerator.setProgressCallback(progressCallback);
        sqlProviderGenerator.setWarnings(warnings);
        return sqlProviderGenerator.getCompilationUnits();
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        // No XML required by the annotated client
        return null;
    }
}
