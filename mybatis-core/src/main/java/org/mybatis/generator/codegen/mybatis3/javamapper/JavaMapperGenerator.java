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
package org.mybatis.generator.codegen.mybatis3.javamapper;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.constant.MpEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.plugins.ExtendModelPlugin;
import org.mybatis.generator.utils.BasePluginUtils;
import org.mybatis.generator.utils.MethodGeneratorUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 */
public class JavaMapperGenerator extends AbstractJavaClientGenerator {

    public JavaMapperGenerator() {
        super(true);
    }

    public JavaMapperGenerator(boolean requiresMatchedXMLGenerator) {
        super(requiresMatchedXMLGenerator);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        /**
         * 基础方法插件类
         **/
        String generatorMapper = context.getProp(ExtendModelPlugin.class.getName(), "generatorMapper");

        String domainPojoName = introspectedTable.getDomainObjectName();
        interfaze.addImportedType(MethodGeneratorUtils.getPoType(context, introspectedTable));

        interfaze.addSuperInterface(new FullyQualifiedJavaType(MpEnum.BaseMapper.getValue() + "<" + domainPojoName + ">"));
        //导入包
        interfaze.addImportedType(new FullyQualifiedJavaType(context.getProp(ExtendModelPlugin.class.getName(), MpEnum.BaseMapper.getValue())));

        if (StringUtility.isTrue(generatorMapper)) {
            if (context.getPlugins().clientGenerated(interfaze, null,
                    introspectedTable)) {
                answer.add(interfaze);
            }

            List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
            if (extraCompilationUnits != null) {
                answer.addAll(extraCompilationUnits);
            }
        }
        return answer;
    }

    protected void initializeAndExecuteGenerator(
            AbstractJavaMapperMethodGenerator methodGenerator,
            Interface interfaze) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addInterfaceElements(interfaze);
    }

    public List<CompilationUnit> getExtraCompilationUnits() {
        return null;
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new XMLMapperGenerator();
    }


    /**
     * 添加java元素
     * param： elementName:元素名字
     * author： guos
     * date 2019/7/15 17:07
     * return
     **/
    private void addJavaElement(Interface interfaze, String elementName) {
        AbstractJavaMapperMethodGenerator methodGenerator = BasePluginUtils.addJavaElement(elementName);
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }


    private String getListType(Object o) {
        return "List<" + o + ">";
    }

}
