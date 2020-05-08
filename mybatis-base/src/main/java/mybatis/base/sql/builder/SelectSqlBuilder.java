package mybatis.base.sql.builder;


import mybatis.base.exception.ColumnUnknowException;
import mybatis.base.meta.EntityField;
import mybatis.base.meta.EntityInfo;
import mybatis.base.sql.builder.condition.*;
import mybatis.core.entity.Condition;
import mybatis.core.entity.LimitCondition;
import mybatis.core.entity.OrderBy;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * select 查询创建
 *
 * @author lgt
 * @date 2019/5/10 : 2:24 PM
 */
public class SelectSqlBuilder extends SqlBuilder {

    private static final String COUNT = " count(*) ";

    private boolean isCount;

    private Set<String> fieldsNames;

    private Set<String> excludeFieldsNames;

    private LimitCondition limitCondition;

    private List<OrderBy> orderByList;

    private boolean forceMaster;

    SelectSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public SelectSqlBuilder excludeFields(Set<String> excludeFieldsNames) {
        this.excludeFieldsNames = excludeFieldsNames;
        return this;
    }

    public SelectSqlBuilder fields(Set<String> fieldsNames) {
        this.fieldsNames = fieldsNames;
        return this;
    }
    public SelectSqlBuilder forceMaster(boolean forceMaster) {
        this.forceMaster = forceMaster;
        return this;
    }



    public SelectSqlBuilder count() {
        this.isCount = true;
        return this;
    }

    public SelectSqlBuilder where(Condition condition) {
        ConditionSqlBuilder conditionSqlBuilder = new ConditionSqlBuilder(getEntityInfo(), condition);
        conditionSqlBuilder.setIsNeedLogicDeleteCondition(!condition.isIgnoreLogicDelete());
        this.setWhereSqlBuilder(conditionSqlBuilder);
        this.orderByList = condition.getOrderByList();
        return this;
    }

    public SelectSqlBuilder limit(int offset, int limit) {
        this.limitCondition = new LimitCondition(offset, limit);
        return this;
    }

    public SelectSqlBuilder limit(LimitCondition condition) {
        this.limitCondition = condition;
        return this;
    }

    public SelectSqlBuilder wherePk() {
        PrimaryKeySqlBuilder primaryKeySqlBuilder = new PrimaryKeySqlBuilder(getEntityInfo());
        this.setWhereSqlBuilder(primaryKeySqlBuilder);
        return this;
    }

    public SelectSqlBuilder where(Object entity) {
        EntitySqlBuilder entitySqlBuilder = new EntitySqlBuilder(getEntityInfo(), entity);
        this.setWhereSqlBuilder(entitySqlBuilder);
        return this;
    }

    @Override
    public String getSql() {
        EntityInfo entityInfo = this.getEntityInfo();
        StringBuilder sql = new StringBuilder();
        if (this.forceMaster) {
            logger.info("主库查询 FORCE_MASTER");
            sql.append(FORCE_MASTER);
        }
        sql.append(SELECT);
        if (isCount) {
            sql.append(COUNT);
        } else {
            List<EntityField> fieldList;
            if (isNotEmpty(fieldsNames)) {
                fieldList = getSelectiveField(fieldsNames);
            } else if (isNotEmpty(excludeFieldsNames)) {
                fieldList = getSelectiveFieldExclude(excludeFieldsNames);
            } else {
                fieldList = getEntityInfo().getEntityFields();
            }
            if (isEmpty(fieldList)) {
                throw new ColumnUnknowException("需要查询的字段为空");
            }
            for (int i = 0; i < fieldList.size(); i++) {
                if (i != 0) {
                    sql.append(SEPARATOR);
                }
                sql.append(wrapColumn(fieldList.get(i).getColumnName()));
            }
        }
        sql.append(FROM);
        sql.append(entityInfo.getTableName());
        // 拼装where条件
        sql.append(getWhereSqlBuilder().getSql());
        if (!isCount) {
            //排序字段
            if (isNotEmpty(this.orderByList)) {
                sql.append(new OrderSqlBuilder(getEntityInfo(), this.orderByList).getSql());
            }
            // 分页字段
            if (Objects.nonNull(this.limitCondition)) {
                sql.append(new LimitSqlBuilder(getEntityInfo(), this.limitCondition).getSql());
            }
        }
        return sql.toString();
    }

}
