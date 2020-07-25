package org.mybatis.generator.utils;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mybatis.generator.utils.DateUtils.date2Str;

/**
 * Description:
 * Author: guos
 * Date: 2019/1/18 17:09
 **/
public class CommentUtils {


    /**
     * param method
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType fullyQualifiedJavaType = method.getReturnType();
        if (fullyQualifiedJavaType == null) {
            return;
        }
        String returnType = fullyQualifiedJavaType.getShortName();
        String description = getDesc(method.getName(), introspectedTable.getRemarks(), introspectedTable.getDomainObjectName());
        List<Parameter> parameters = method.getParameters();
        method.addJavaDocLine("");
        method.addJavaDocLine("/**");
        method.addJavaDocLine("* " + description);
        if (parameters != null && parameters.size() > 0) {
            for (Parameter parameter : parameters) {
                if (parameter.getName().contains(" ")) {
                    method.addJavaDocLine("* @param " + parameter.getName().split(" ")[1] + " " + parameter.getName().split(" ")[1]);
                } else {
                    method.addJavaDocLine("* @param " + parameter.getName() + " " + parameter.getName());
                }
            }
        }
        method.addJavaDocLine("* @author " + introspectedTable.getContext().getProperty("author"));
        method.addJavaDocLine("* @date " + date2Str(new Date()));
        if (!"void".equals(returnType)) {
            method.addJavaDocLine("* @return " + returnType);
        }
        method.addJavaDocLine("*/");
    }


    /**
     * param method
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static String addGeneralMethodComment(String methodStr, String methodName, String parameterName, String returnType, IntrospectedTable introspectedTable) {
        String description = getDesc(methodName, introspectedTable.getRemarks(), introspectedTable.getDomainObjectName());
        String TabStr = "\t\t\t";
        StringBuilder sb = new StringBuilder();
        sb.append(TabStr + "" + "\n");
        sb.append(TabStr + "/**" + "\n");
        sb.append(TabStr + "*" + description + "\n");
        sb.append("* @param " + parameterName + " " + parameterName + "\n");
        sb.append("* @author " + introspectedTable.getContext().getProperty("author") + "\n");
        sb.append("* @date " + date2Str(new Date()) + "\n");
        if (!"void".equals(returnType)) {
            sb.append("* @return " + returnType + "\n");
        }
        sb.append("*/" + "\n");
        sb.append(methodStr + "\n");
        return sb.toString();
    }


    /**
     * 类注释
     * param topLevelClass
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addGeneralClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        commentTemplate(2, 4, introspectedTable, topLevelClass, null);
    }

    /**
     * BusinessImpl注释
     * param topLevelClass
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addBusinessClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        commentTemplate(2, 5, introspectedTable, topLevelClass, null);
    }


    /**
     * Business注释
     * param topLevelClass
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addBusinessComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        commentTemplate(1, 5, introspectedTable, topLevelClass, null);
    }

    /**
     * Controller注释
     * param topLevelClass
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addControllerClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        commentTemplate(2, 6, introspectedTable, topLevelClass, null);
    }


    /**
     * po注释
     * param topLevelClass
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        commentTemplate(2, 1, introspectedTable, topLevelClass, null);
    }


    /**
     * 接口注释
     * param inter
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addGeneralInterfaceComment(Interface inter, IntrospectedTable introspectedTable) {
        commentTemplate(1, 3, introspectedTable, null, inter);
    }


    /**
     * mapper注释
     * param inter
     * param introspectedTable
     * Description:
     * Author: guos
     * Date: 2019/1/23 10:43
     * Return:
     **/
    public static void addMapperComment(Interface inter, IntrospectedTable introspectedTable) {
        commentTemplate(1, 2, introspectedTable, null, inter);
    }


    /**
     * Description:注释模板
     * param type ：1，接口 2，类
     * param flag ：1，po类 2，mapper 3，service接口 4,service实现类
     * Author: guos
     *
     * @Date: 2019/1/23 10:43
     * @Return:
     **/
    private static void commentTemplate(int type, int flag, IntrospectedTable introspectedTable, TopLevelClass topLevelClass, Interface inter) {
        String remarks = introspectedTable.getRemarks(); //标注释
        String line1 = "/**";
        String line2 = "* ";
        String line3 = "* @author " + introspectedTable.getContext().getProperty("author");
        String line4 = "* @date " + date2Str(new Date());
        String line5 = "*/";
        String desc = remarks;

        if (1 == flag) {
            String baseShortName = topLevelClass.getType().getShortName();
            if (baseShortName.endsWith(CommonConstant.QUERY_SUFFIX)) {
                desc = desc + "查询条件类";
            } else if (baseShortName.endsWith(CommonConstant.VO_SUFFIX)) {
                desc = desc + "显示类";
            } else {
                desc = desc + "实体类";
            }
        } else if (2 == flag) {
            desc = desc + "数据访问层";
        } else if (3 == flag) {
            desc = desc + "service类";
        } else if (4 == flag) {
            desc = desc + "service实现类";
        } else if (5 == flag) {
            desc = desc + "业务类";
        } else if (6 == flag) {
            desc = desc + "api类";
        }
        line2 = line2 + desc;
        List<String> lines = Arrays.asList(line1, line2, line3, line4, line5);
        if (1 == type) {
            for (String line : lines) {
                inter.addJavaDocLine(line);
            }
        } else {
            for (String line : lines) {
                topLevelClass.addJavaDocLine(line);
            }
        }
    }

    public static String getTransferredStr(String str) {
        return "\"" + str + "\"";
    }

    private static String getDesc(String methodName, String desc, String domainName) {
        String remark = "";
        if (MethodEnum.GET.getValue().equals(methodName)) {
            remark = "查询" + desc;
        } else if (MethodEnum.GET_ONE.getValue().equals(methodName)) {
            remark = "根据" + MethodUtils.toLowerCase(domainName) + "查询" + desc;
        } else if (MethodEnum.SAVE.getValue().equals(methodName)) {
            remark = "新增" + desc;
        } else if (MethodEnum.DELETE_BY_CONDITION.getValue().equals(methodName)) {
            remark = "根据条件物理删除" + desc;
        } else if (MethodEnum.SAVE_AND_GET.getValue().equals(methodName)) {
            remark = "新增并返回" + desc;
        } else if (MethodEnum.UPDATE.getValue().equals(methodName)) {
            remark = "更新" + desc;
        } else if (MethodEnum.COUNT.getValue().equals(methodName)) {
            remark = "根据po查询" + desc + "总数";
        } else if (MethodEnum.LIST.getValue().equals(methodName)) {
            remark = "根据po查询" + desc + "列表";
        } else if (MethodEnum.COUNT_BY_CONDITION.getValue().equals(methodName)) {
            remark = "根据条件类查询" + desc + "总数";
        } else if (MethodEnum.LIST_BY_CONDITION.getValue().equals(methodName)) {
            remark = "根据条件类查询" + desc + "列表";
        } else if (MethodEnum.MAP.getValue().equals(methodName) || MethodEnum.MAP_BY_CONDITION.getValue().equals(methodName) || MethodEnum.MAP_BY_IDS.getValue().equals(methodName)) {
            remark = "将符合查询条件的" + desc + "列表转map";
        } else if (MethodEnum.BATCH_LIST.getValue().equals(methodName)) {
            remark = "分批查询" + desc;
        } else if (MethodEnum.DO_BATCH.getValue().equals(methodName)) {
            remark = "处理" + desc + "分批查询";
        }
        return remark;
    }
}
