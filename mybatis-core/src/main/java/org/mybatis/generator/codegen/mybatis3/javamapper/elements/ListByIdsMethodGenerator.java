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
package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.utils.AnnotationUtils;

import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class ListByIdsMethodGenerator extends
        AbstractJavaMapperMethodGenerator {

    public ListByIdsMethodGenerator() {
        super();
    }
    private boolean isSimple;

    public ListByIdsMethodGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        //先创建import对象
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        //添加Lsit的包
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        //创建方法对象
        Method method = new Method();
        //设置该方法为public
        method.setVisibility(JavaVisibility.PUBLIC);
        //设置返回类型是List
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType
                .getNewListInstance();
        FullyQualifiedJavaType listType;
        //设置List的类型是实体类的对象
        listType = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        importedTypes.add(listType);
        //返回类型对象设置为List
        returnType.addTypeArgument(listType);
        //方法对象设置返回类型对象
        method.setReturnType(returnType);
        //设置方法名称为我们在IntrospectedTable类中初始化的 “listByIds”
        method.setName(introspectedTable.getListByIdsStatementId());

        //设置参数类型是List
        FullyQualifiedJavaType parameterType = FullyQualifiedJavaType
                .getNewListInstance();
        FullyQualifiedJavaType parameterListType = new FullyQualifiedJavaType(
                introspectedTable.getPrimaryKeyColumns().get(0)
                        .getFullyQualifiedJavaType().toString());
        importedTypes.add(parameterListType);
        parameterType.addTypeArgument(parameterListType);


        /*if (isSimple) {
            parameterType = new FullyQualifiedJavaType(
                    introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules()
                    .calculateAllFieldsClass();
        }
        //import参数类型对象
        importedTypes.add(parameterType);*/
        //为方法添加参数，变量名称record
        method.addParameter(new Parameter(parameterType, "ids")); //$NON-NLS-1$
        //
        //addMapperAnnotations(interfaze, method);

        //添加缓存注解
        AnnotationUtils.addAnnotation(method,introspectedTable,interfaze);


        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);
        if (context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
        return;
    }
}
