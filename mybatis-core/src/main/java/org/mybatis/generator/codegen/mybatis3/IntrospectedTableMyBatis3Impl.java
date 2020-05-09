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
package org.mybatis.generator.codegen.mybatis3;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.AbstractGenerator;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.AnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.MixedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.model.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.plugins.ExtendModelPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Butler
 */
public class IntrospectedTableMyBatis3Impl extends IntrospectedTable {

    protected List<AbstractJavaGenerator> javaModelGenerators;

    protected List<AbstractJavaGenerator> clientGenerators;

    protected AbstractXmlGenerator xmlMapperGenerator;

    public IntrospectedTableMyBatis3Impl() {
        super(TargetRuntime.MYBATIS3);
        javaModelGenerators = new ArrayList<AbstractJavaGenerator>();
        clientGenerators = new ArrayList<AbstractJavaGenerator>();
    }

    @Override
    public void calculateGenerators(List<String> warnings,
                                    ProgressCallback progressCallback) {
        calculateJavaModelGenerators(warnings, progressCallback);
        AbstractJavaClientGenerator javaClientGenerator =
                calculateClientGenerators(warnings, progressCallback);
        calculateXmlMapperGenerator(javaClientGenerator, warnings, progressCallback);
    }

    protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator,
                                               List<String> warnings,
                                               ProgressCallback progressCallback) {
        if (javaClientGenerator == null) {
            if (context.getSqlMapGeneratorConfiguration() != null) {
                xmlMapperGenerator = new XMLMapperGenerator();
            }
        } else {
            xmlMapperGenerator = javaClientGenerator.getMatchedXMLGenerator();
        }

        initializeAbstractGenerator(xmlMapperGenerator, warnings,
                progressCallback);
    }

    protected AbstractJavaClientGenerator calculateClientGenerators(List<String> warnings,
                                                                    ProgressCallback progressCallback) {
        if (!rules.generateJavaClient()) {
            return null;
        }

        AbstractJavaClientGenerator javaGenerator = createJavaClientGenerator();
        if (javaGenerator == null) {
            return null;
        }

        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        clientGenerators.add(javaGenerator);

        return javaGenerator;
    }

    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return null;
        }

        String type = context.getJavaClientGeneratorConfiguration()
                .getConfigurationType();
        AbstractJavaClientGenerator javaGenerator;
        if ("XMLMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new JavaMapperGenerator();
        } else if ("MIXEDMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new MixedClientGenerator();
        } else if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new AnnotatedClientGenerator();
        } else if ("MAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new JavaMapperGenerator();
        } else {
            javaGenerator = (AbstractJavaClientGenerator) ObjectFactory
                    .createInternalObject(type);
        }

        return javaGenerator;
    }

    protected void calculateJavaModelGenerators(List<String> warnings,
                                                ProgressCallback progressCallback) {
        if (getRules().generateExampleClass()) {
            AbstractJavaGenerator javaGenerator = new ExampleGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        if (getRules().generatePrimaryKeyClass()) {
            AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        // 生成查询po
        String generatorPo = context.getProp(ExtendModelPlugin.class.getName(), "generatorPo");
        if (StringUtility.stringHasValue(generatorPo) && StringUtility.isTrue(generatorPo)) {
            if (getRules().generateBaseRecordClass()) {
                AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
                initializeAbstractGenerator(javaGenerator, warnings,
                        progressCallback);
                javaModelGenerators.add(javaGenerator);
            }
        }

        if (getRules().generateRecordWithBLOBsClass()) {
            AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        //生成ao
        if (context.isCustomEnable(ExtendModelPlugin.class.getName(), "aoProject")) {
            BaseRecordAOGenerator aoGenerator = new BaseRecordAOGenerator(context);
            initializeAbstractGenerator(aoGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(aoGenerator);
        }


        //生成vo
        if (context.isCustomEnable(ExtendModelPlugin.class.getName(), "voProject")) {
            BaseRecordVoGenerator voGenerator = new BaseRecordVoGenerator(context);
            initializeAbstractGenerator(voGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(voGenerator);
        }
    }

    protected void initializeAbstractGenerator(
            AbstractGenerator abstractGenerator, List<String> warnings,
            ProgressCallback progressCallback) {
        if (abstractGenerator == null) {
            return;
        }

        abstractGenerator.setContext(context);
        abstractGenerator.setIntrospectedTable(this);
        abstractGenerator.setProgressCallback(progressCallback);
        abstractGenerator.setWarnings(warnings);
    }

    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();
        for (AbstractJavaGenerator javaGenerator : javaModelGenerators) {
            //不生成： xxxExample类
            if (javaGenerator instanceof ExampleGenerator) {
                continue;
            }
            List<CompilationUnit> compilationUnits = javaGenerator
                    .getCompilationUnits();
            if (compilationUnits == null || compilationUnits.size() == 0) {
                continue;
            }
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                        javaGenerator.getModelTargetProject(),
                        context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                        context.getJavaFormatter());
                answer.add(gjf);
            }
        }
        boolean enableMapper = false;
        String generatorMapper = context.getProp(ExtendModelPlugin.class.getName(), "generatorMapper");
        if (StringUtility.stringHasValue(generatorMapper) || StringUtility.isTrue(generatorMapper)) {
            enableMapper = true;
        }
        if (enableMapper) {
            for (AbstractJavaGenerator javaGenerator : clientGenerators) {
                List<CompilationUnit> compilationUnits = javaGenerator
                        .getCompilationUnits();
                for (CompilationUnit compilationUnit : compilationUnits) {
                    GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                            context.getJavaClientGeneratorConfiguration().getTargetProject(),
                            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                            context.getJavaFormatter());
                    answer.add(gjf);
                }
            }

        }
        return answer;
    }

    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();
        boolean enableXml = false;
        String generatorXml = context.getProp(ExtendModelPlugin.class.getName(), "generatorXml");
        if (StringUtility.stringHasValue(generatorXml) || StringUtility.isTrue(generatorXml)) {
            enableXml = true;
        }
        if (enableXml) {
            if (xmlMapperGenerator != null) {
                Document document = xmlMapperGenerator.getDocument();
                GeneratedXmlFile gxf = new GeneratedXmlFile(document,
                        getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(),
                        context.getSqlMapGeneratorConfiguration().getTargetProject(),
                        true, context.getXmlFormatter());
                if (context.getPlugins().sqlMapGenerated(gxf, this)) {

                    answer.add(gxf);
                }
            }
        }
        return answer;
    }

    @Override
    public int getGenerationSteps() {
        return javaModelGenerators.size()
                + clientGenerators.size()
                + (xmlMapperGenerator == null ? 0 : 1);
    }

    @Override
    public boolean isJava5Targeted() {
        return true;
    }

    @Override
    public boolean requiresXMLGenerator() {
        AbstractJavaClientGenerator javaClientGenerator =
                createJavaClientGenerator();

        if (javaClientGenerator == null) {
            return false;
        } else {
            return javaClientGenerator.requiresXMLGenerator();
        }
    }
}
