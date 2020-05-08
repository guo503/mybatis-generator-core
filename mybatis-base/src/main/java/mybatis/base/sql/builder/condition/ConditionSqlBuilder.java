package mybatis.base.sql.builder.condition;


import mybatis.base.exception.ColumnUnknowException;
import mybatis.base.exception.ParamErrorException;
import mybatis.base.meta.EntityField;
import mybatis.base.meta.EntityInfo;
import mybatis.base.meta.LogicDeleteField;
import mybatis.core.entity.Condition;
import mybatis.core.entity.ParamCondition;
import mybatis.core.entity.ParamConstant;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConditionSqlBuilder extends WhereSqlBuilder {

    private Condition condition;

    ConditionSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public ConditionSqlBuilder(EntityInfo entityInfo, Condition condition) {
        super(entityInfo);
        this.condition = condition;
    }

    @Override
    protected StringBuilder getWhereSql() {
        List<Condition.Criteria> criteriaList = this.condition.getCriteriaList();
        //如果条件都是空的，则删除所有条件
        criteriaList.removeIf(criteria -> isEmpty(criteria.getParamConditions()));
        Map<String, Object> params = this.condition.getParams();
        StringBuilder conditionSql = new StringBuilder();
        if (isNotEmpty(criteriaList)) {
            //只有一个criteria
            if (criteriaList.size() == 1) {
                parserCriteria(conditionSql, criteriaList.get(0), params);
            } else {
                for (int i = 0; i < criteriaList.size(); i++) {
                    Condition.Criteria criteria = criteriaList.get(i);
                    //每一个条件的关系 空 ，OR， AND
                    if (i != 0 && hasLength(criteria.getCriRel())) {
                        conditionSql.append(criteria.getCriRel());
                    }
                    parserCriteriaClose(conditionSql, criteria, params);
                }
                if (isNeedLogicDelete() && isHasCondition()) {
                    conditionSql.insert(0, OPEN_PLACE);
                    conditionSql.append(CLOSED_PLACE);
                }
            }
        }
        return conditionSql;
    }

    private void parserCriteria(StringBuilder conditionSql, Condition.Criteria criteria, Map<String, Object> params) {
        List<ParamCondition> conditions = criteria.getParamConditions();
        if (isEmpty(conditions)) {
            throw new ParamErrorException("创建的第" + criteria.getIndex() + "个criteria为空");
        }
        LogicDeleteField logicDeleteField = getEntityInfo().getLogicDeleteField();
        for (int i = 0; i < conditions.size(); i++) {
            ParamCondition param = conditions.get(i);
            if (Objects.nonNull(logicDeleteField)
                    && Objects.equals(logicDeleteField.getFieldName(), param.getFieldName())) {
                setHasDeleteCondition(true);
            }
            //至少有一个有效条件
            if (!isHasCondition()) {
                setHasCondition(true);
            }
            if (i != 0) {
                conditionSql.append(AND);
            }
            EntityField field = getEntityInfo().getFieldNameMap().get(param.getFieldName());
            if (Objects.isNull(field)) {
                throw new ColumnUnknowException("根据属性名查询不到该字段 :" + param.getFieldName());
            }
            conditionSql.append(wrapColumn(field.getColumnName()))
                    .append(param.getParamSymbol().getValue());

            if (param.getParamSymbol().getParamType() == ParamConstant.PARAM_SINGLE) {
                //普通参数
                conditionSql.append(String.format(REPLACE_PLACE, param.getParamPlaceName()));
            } else if (param.getParamSymbol().getParamType() == ParamConstant.PARAM_COLLECTION) {
                //数组参数
                Object object = params.get(param.getParamName());
                if (Objects.isNull(object)) {
                    throw new ParamErrorException("参数" + param.getFieldName() + "的为类型为数组类型，当前参数为空;");
                }
                if (!(object instanceof Collection)) {
                    throw new ParamErrorException("参数" + param.getFieldName() + "的为类型为数组类型,而当前参数为" + object.getClass());
                }
                Collection collection = (Collection) object;
                if (collection.isEmpty()) {
                    throw new ParamErrorException("参数" + param.getFieldName() + "的为数组类型，但是内容为空");
                }
                int collParamIndex = 0;
                conditionSql.append(OPEN_PLACE);
                for (Object p : collection) {
                    String collParamPlace = param.getParamPlaceName() + "_" + collParamIndex;
                    String collParamMapName = param.getParamName() + "_" + collParamIndex;
                    params.put(collParamMapName, p);
                    if (collParamIndex != 0) {
                        conditionSql.append(SEPARATOR);
                    }
                    conditionSql.append(String.format(REPLACE_PLACE, collParamPlace));
                    collParamIndex++;
                }
                conditionSql.append(CLOSED_PLACE);

            }
        }
    }

    private void parserCriteriaClose(StringBuilder conditionSql, Condition.Criteria criteria, Map<String, Object> params) {
        conditionSql.append(OPEN_PLACE);
        parserCriteria(conditionSql, criteria, params);
        conditionSql.append(CLOSED_PLACE);
    }

}
