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
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.plugins.ExtendModelPlugin;
import org.mybatis.generator.utils.*;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 */
public class JavaMapperGenerator extends AbstractJavaClientGenerator {

    /**
     * extentModel插件类
     **/
    private PluginConfiguration extentModelPlugin;

    /**
     * 基础方法插件类
     **/
    private PluginConfiguration baseMethodPlugin;

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
        extentModelPlugin = ContextUtils.getPlugin(context, CommonConstant.EXTEND_MODEL_PLUGIN);

        baseMethodPlugin = ContextUtils.getPlugin(context, CommonConstant.BASE_METHOD_PLUGIN);
        String generatorMapper =context.getProp(ExtendModelPlugin.class.getName(),"generatorMapper");

        String domainPojoName = introspectedTable.getDomainObjectName();
        interfaze.addImportedType(MethodGeneratorUtils.getPoType(context, introspectedTable));

        String mapperPath = context.getProp(ExtendModelPlugin.class.getName(),"mapper");
        String softDeleteMapperPath =context.getProp(ExtendModelPlugin.class.getName(),"softDeleteMapper");
        //继承Mapper,SoftDeleteMapper
        interfaze.addSuperInterface(new FullyQualifiedJavaType(MethodUtils.getClassName(mapperPath, ".") + "<" + domainPojoName + ">"));
        interfaze.addSuperInterface(new FullyQualifiedJavaType(MethodUtils.getClassName(softDeleteMapperPath, ".") + "<" + domainPojoName + ">"));
        //导入包
        interfaze.addImportedType(new FullyQualifiedJavaType(mapperPath));
        interfaze.addImportedType(new FullyQualifiedJavaType(softDeleteMapperPath));

        //String suffix = ".Java";
        //mapper.xml文件路径
        //String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        //String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        //String mapperPath = targetProject + LocalFileUtils.getPath(targetPackage) + File.separator + domainPojoName + "Mapper" + suffix;
        //File mapperFile = new File(mapperPath);
        //String mapperPackStr = "package " + targetPackage + ";";
        //boolean mapperFileExist = mapperFile.exists();
        if (StringUtility.isTrue(generatorMapper)) {
            //this.addJavaElement(interfaze, MethodEnum.UPDATE.getName());
            //generateMapper(interfaze, answer, domainPojoName, mapperPath, mapperPackStr, mapperFileExist);
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

    private void generateMapper(Interface interfaze, List<CompilationUnit> answer, String domainPojoName, String mapperPath, String mapperPackStr, boolean mapperFileExist) {
        boolean hasModify = false;
        if (mapperFileExist) {
            String param = MethodUtils.toLowerCase(domainPojoName);
            String cond = MethodGeneratorUtils.getCondName(domainPojoName);
            String condParam = MethodUtils.toLowerCase(cond);
            //get
            String getMethod = MapperXmlUtils.getEnableSelectByPrimaryKey(baseMethodPlugin);
            String getSign = MethodUtils.getMethodSign(getMethod, "Integer", "id", domainPojoName);
            if (!LocalFileUtils.findStr(mapperPath, getSign)) {
                LocalFileUtils.modifyLine(mapperPath, "}", "\t" + MethodUtils.getMethodStr(getMethod, "Integer", "id", domainPojoName, introspectedTable, context) + "\n\n\n}");
                hasModify = true;
            }
            //getPo
          /*  String getPoMethod=MethodUtils.getSelectPoMethodStr(domainPojoName);
            String getPoSign = MethodUtils.getMethodSign(getPoMethod, domainPojoName, MethodUtils.toLowerCase(domainPojoName), domainPojoName);
            if (!LocalFileUtils.findStr(mapperPath, getPoSign)) {
                LocalFileUtils.modifyLine(mapperPath, "}", "\t" + MethodUtils.getMethodStr(getPoMethod,domainPojoName,MethodUtils.toLowerCase(domainPojoName),domainPojoName,introspectedTable,context)  + "\n\n\n}");
                hasModify = true;
            }*/
            //save
            String saveMethod = MapperXmlUtils.getEnableInsertSelective(baseMethodPlugin);
            String saveSign = MethodUtils.getMethodSign(saveMethod, domainPojoName, param, "int");
            if (!LocalFileUtils.findStr(mapperPath, saveSign)) {
                LocalFileUtils.modifyLine(mapperPath, "}", "\t" + MethodUtils.getMethodStr(saveMethod, domainPojoName, param, "int", introspectedTable, context) + "\n\n\n}");
                hasModify = true;
            }
            //update
            String updateMethod = MapperXmlUtils.getEnableUpdateByPrimaryKeySelective(baseMethodPlugin);
            String updateSign = MethodUtils.getMethodSign(updateMethod, domainPojoName, param, "int");
            if (!LocalFileUtils.findStr(mapperPath, updateSign)) {
                LocalFileUtils.modifyLine(mapperPath, "}", "\t" + MethodUtils.getMethodStr(updateMethod, domainPojoName, param, "int", introspectedTable, context) + "\n\n\n}");
                hasModify = true;
            }
            //listByIds
            String listByIdsMethod = MapperXmlUtils.getListByIds(baseMethodPlugin);
            String listByIdsSign = MethodUtils.getMethodSign(listByIdsMethod, this.getListType("Integer"), "ids", this.getListType(domainPojoName));
            if (!LocalFileUtils.findStr(mapperPath, listByIdsSign)) {
                LocalFileUtils.modifyLine(mapperPath, "}", "\t" + MethodUtils.getMethodStr(listByIdsMethod, this.getListType("Integer"), "ids", this.getListType(domainPojoName), introspectedTable, context) + "\n\n\n}");
                hasModify = true;
            }
            //list
            String listMethod = MapperXmlUtils.getListByCondition(baseMethodPlugin);
            String listSign = MethodUtils.getMethodSign(listMethod, cond, condParam, this.getListType(domainPojoName));
            if (!LocalFileUtils.findStr(mapperPath, listSign)) {
                LocalFileUtils.modifyLine(mapperPath, "}", "\t" + MethodUtils.getMethodStr(listMethod, cond, condParam, this.getListType(domainPojoName), introspectedTable, context) + "\n\n\n}");
                hasModify = true;
            }
            //count
            String countMethod = MapperXmlUtils.getCountByCondition(baseMethodPlugin);
            String countSign = MethodUtils.getMethodSign(countMethod, cond, condParam, "int");
            if (!LocalFileUtils.findStr(mapperPath, countSign)) {
                LocalFileUtils.modifyLine(mapperPath, "}", "\t" + MethodUtils.getMethodStr(countMethod, cond, condParam, "int", introspectedTable, context) + "\n\n\n}");
                hasModify = true;
            }
            if (hasModify) {
                LocalFileUtils.modifyLine(mapperPath, mapperPackStr, mapperPackStr + "\n\n\nimport java.util.*;");
            }
        } else {
            //addDeleteByPrimaryKeyMethod(interfaze);
            //addInsertMethod(interfaze);
            this.addJavaElement(interfaze, MethodEnum.GET.getName());
            this.addJavaElement(interfaze, MethodEnum.SAVE.getName());
            this.addJavaElement(interfaze, MethodEnum.DELETE_BY_CONDITION.getName());
            this.addJavaElement(interfaze, MethodEnum.UPDATE.getName());
            this.addJavaElement(interfaze, MethodEnum.LIST_BY_IDS.getName());
            this.addJavaElement(interfaze, MethodEnum.LIST.getName());
            this.addJavaElement(interfaze, MethodEnum.COUNT.getName());


            //addSelectByPrimaryKeyMethod(interfaze);
            //addSelectBySelectiveMethod(interfaze);
            //addInsertSelectiveMethod(interfaze);
            //addDeleteByConditionMethod(interfaze);
            //addUpdateByPrimaryKeySelectiveMethod(interfaze);
            // addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
            //addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);
            //addListByIdsMethod(interfaze);
            //addListByConditionMethod(interfaze);
            //addCountByConditionMethod(interfaze);


            if (context.getPlugins().clientGenerated(interfaze, null,
                    introspectedTable)) {
                answer.add(interfaze);
            }

            List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
            if (extraCompilationUnits != null) {
                answer.addAll(extraCompilationUnits);
            }

        }
    }

    protected void addCountByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateCountByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new CountByExampleMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByExampleMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByExampleWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByExampleWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectBySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectBySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
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


    protected void addListByIdsMethod(Interface interfaze) {
        if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new ListByIdsMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }


    private String getListType(Object o) {
        return "List<" + o + ">";
    }

}
