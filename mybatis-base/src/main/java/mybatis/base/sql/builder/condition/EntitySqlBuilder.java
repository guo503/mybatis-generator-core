package mybatis.base.sql.builder.condition;


import mybatis.base.helper.EntityHelper;
import mybatis.base.meta.EntityField;
import mybatis.base.meta.EntityInfo;
import mybatis.base.meta.LogicDeleteField;
import mybatis.core.entity.ParamConstant;
import mybatis.core.entity.ParamSymbol;

import java.util.Objects;

public class EntitySqlBuilder extends WhereSqlBuilder {

    private Object entity;

    EntitySqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public EntitySqlBuilder(EntityInfo entityInfo, Object entity) {
        super(entityInfo);
        this.entity = entity;
    }

    @Override
    protected StringBuilder getWhereSql() {
        StringBuilder conditionSql = new StringBuilder();
        try {
            LogicDeleteField logicDeleteField = getEntityInfo().getLogicDeleteField();
            int index = 0;
            EntityHelper entityHelper = new EntityHelper(getEntityInfo(), this.entity);
            for (EntityField field : getEntityInfo().getEntityFields()) {
                field.getField().setAccessible(true);
                Object obj = entityHelper.getFieldValue(field);
                if (Objects.isNull(obj)) {
                    continue;
                }
                if (!isHasCondition()) {
                    setHasCondition(true);
                }
                //如果查询字段中有逻辑删除字段，则标记是否条件有删除字段为true
                if (Objects.nonNull(logicDeleteField)
                        && Objects.equals(logicDeleteField.getFieldName(), field.getFieldName())) {
                    setHasDeleteCondition(true);
                }

                if (index++ != 0) {
                    conditionSql.append(AND);
                }
                conditionSql.append(wrapColumn(field.getColumnName()))
                        .append(ParamSymbol.EQUAL.getValue())
                        .append(String.format(REPLACE_PLACE, ParamConstant.ENTITY + DOT + field.getFieldName()));
            }
        } catch (IllegalAccessException e) {
            logger.error("获取成员属性对象失败", e);
        }
        return conditionSql;
    }

}
