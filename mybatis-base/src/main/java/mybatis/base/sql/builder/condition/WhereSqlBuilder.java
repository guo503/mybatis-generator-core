package mybatis.base.sql.builder.condition;


import mybatis.base.meta.EntityInfo;
import mybatis.base.meta.ParamValue;
import mybatis.base.sql.builder.CommonBuilder;
import mybatis.core.entity.ParamSymbol;

import java.util.Objects;

public abstract class WhereSqlBuilder extends CommonBuilder {

    private boolean isHasCondition;

    private boolean isHasDeleteCondition;

    private boolean isNeedLogicDeleteCondition = true;

    WhereSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    protected abstract StringBuilder getWhereSql();

    @Override
    public String getSql() {
        StringBuilder whereSql = this.getWhereSql();
        this.appendLogicDelete(whereSql);
        if (!isHasCondition) {
            return EMPTY_SQL;
        }
        return WHERE + whereSql.toString();
    }

    public boolean isHasCondition() {
        return this.isHasCondition;
    }

    public void setHasCondition(boolean hasCondition) {
        isHasCondition = hasCondition;
    }

    public boolean isHasDeleteCondition() {
        return isHasDeleteCondition;
    }

    public void setHasDeleteCondition(boolean hasDeleteCondition) {
        this.isHasDeleteCondition = hasDeleteCondition;
    }

    public void setIsNeedLogicDeleteCondition(boolean isNeedLogicDeleteCondition) {
        this.isNeedLogicDeleteCondition = isNeedLogicDeleteCondition;
    }

    private void appendLogicDelete(StringBuilder conditionSql) {
        //是否逻辑删除
        if (isNeedLogicDelete()) {
            if (this.isHasCondition) {
                conditionSql.append(AND);
            }
            conditionSql.append(getParam(new ParamValue(getEntityInfo().getLogicDeleteField().getColumnName(), ParamSymbol.EQUAL, getEntityInfo().getLogicDeleteField().getIsNotDelete())));
        }
    }

    protected boolean isNeedLogicDelete() {
        return isNeedLogicDeleteCondition && !this.isHasDeleteCondition && Objects.nonNull(getEntityInfo().getLogicDeleteField());
    }

    protected String getParam(ParamValue paramValue) {
        if (!isHasCondition) {
            this.setHasCondition(true);
        }
        if (hasLength(paramValue.getReplacePlaceName())) {
            return wrapColumn(paramValue.getColumnName())
                    + paramValue.getParamSymbol().getValue()
                    + String.format(REPLACE_PLACE, paramValue.getReplacePlaceName());
        }
        return wrapColumn(paramValue.getColumnName())
                + paramValue.getParamSymbol().getValue()
                + paramValue.getValue();
    }

}
