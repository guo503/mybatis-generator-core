package mybatis.spring.dao;


import mybatis.base.helper.EntityHelper;
import mybatis.base.meta.EntityInfo;
import mybatis.core.entity.Condition;
import mybatis.spring.dao.rowmapper.ParamBoundSql;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GeneralSqlSourceParser extends BaseBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GeneralSqlSourceParser.class);
    private static final String OPEN_TOKEN = "#{";
    private static final int OPEN_TOKEN_LENGTH = OPEN_TOKEN.length();
    private static final String REPLACE_TOKEN = "?";

    private static final String CLOSE_TOKEN = "}";
    private static final int CLOSE_TOKEN_LENGTH = CLOSE_TOKEN.length();




    public GeneralSqlSourceParser(Configuration configuration) {
        super(configuration);
    }

    public ParamBoundSql parse(EntityInfo entityInfo, String origin, Object entity) {
        StringBuilder sql = new StringBuilder();
        List<String> paramNameList = parseOriginSql(origin, sql);
        Object[] params = parseEntity(entity, paramNameList,entityInfo);
        return new ParamBoundSql(params, sql.toString());

    }


    public ParamBoundSql parse(EntityInfo entityInfo,String origin, Condition condition) {
        StringBuilder sql = new StringBuilder();
        return null;
    }



    private Object[] parseEntity(Object entity, List<String> paramNameList,EntityInfo entityInfo) {

        if (!entity.getClass().equals(entityInfo.getEntityClass())) {
            return new Object[]{entity};
        }
        Object[] params = new Object[paramNameList.size()];
        EntityHelper helper = new EntityHelper(entityInfo, entity);
        for (int i = 0; i < paramNameList.size(); i++) {
            String paramName = paramNameList.get(i);
            if (paramName.contains(".")) {
                String[] splitStr = paramName.split("\\.");
                paramName = splitStr[splitStr.length - 1];
            }
            try {
                params[i] = helper.getFieldValue(paramName);
            } catch (IllegalAccessException e) {
                logger.info("获取不到参数名：{}的值", paramName);
            }
        }
        return params;
    }

    private List<String> parseOriginSql(String origin, StringBuilder sql) {
        List<String> paramNameList = new ArrayList<>();
        int index = origin.indexOf(OPEN_TOKEN);
        int beginIndex = 0;
        while (index > 0) {
            sql.append(origin, beginIndex, index)
                    .append(REPLACE_TOKEN);
            int closeIndex = origin.indexOf(CLOSE_TOKEN, index);
            paramNameList.add(origin.substring(index + OPEN_TOKEN_LENGTH, closeIndex));
            index = origin.indexOf(OPEN_TOKEN, closeIndex);
            beginIndex = closeIndex + CLOSE_TOKEN_LENGTH;
        }
        sql.append(origin, beginIndex, origin.length());
        return paramNameList;
    }

}
