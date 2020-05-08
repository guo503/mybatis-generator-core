package mybatis.base.sql.builder;


import mybatis.base.exception.ColumnUnknowException;
import mybatis.base.meta.EntityInfo;
import mybatis.base.meta.LogicDeleteField;
import mybatis.base.meta.VersionField;
import mybatis.base.sql.builder.condition.PrimaryKeySqlBuilder;

import java.util.Objects;

/**
 * 删除sql构造器
 *
 * @author lgt
 * @date 2019/5/22 : 10:22 AM
 */
public class DeleteSqlBuilder extends SqlBuilder {

    private boolean isLogicDelete;

    protected DeleteSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public DeleteSqlBuilder wherePk() {
        PrimaryKeySqlBuilder primaryKeySqlBuilder = new PrimaryKeySqlBuilder(getEntityInfo());
        this.setWhereSqlBuilder(primaryKeySqlBuilder);
        return this;
    }

    public DeleteSqlBuilder logicDelete() {
        this.isLogicDelete = true;
        return this;
    }

    @Override
    public String getSql() {
        if (isLogicDelete) {
            if (Objects.isNull(getEntityInfo().getLogicDeleteField())) {
                throw new ColumnUnknowException("没有逻辑删除字段");
            }
            return new LogicDeleteSqlBuilder(getEntityInfo(), getEntityInfo().getLogicDeleteField()).getSql();
        }

        StringBuilder sql = new StringBuilder(DELETE);
        // 不需要根据逻辑删除条件
        this.getWhereSqlBuilder().setIsNeedLogicDeleteCondition(false);
        sql.append(FROM)
                .append(getEntityInfo().getTableName())
                .append(getWhereSqlBuilder().getSql());

        return sql.toString();
    }

    class LogicDeleteSqlBuilder extends SqlBuilder {

        private LogicDeleteField logicDeleteField;

        LogicDeleteSqlBuilder(EntityInfo entityInfo, LogicDeleteField logicDeleteField) {
            super(entityInfo);
            this.logicDeleteField = logicDeleteField;
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder(UPDATE);
            sql.append(this.getEntityInfo().getTableName());
            sql.append(SET);
            sql.append(wrapColumn(this.logicDeleteField.getColumnName()))
                    .append(EQUAL)
                    .append(logicDeleteField.getIsDelete());
            VersionField versionField = getEntityInfo().getVersionField();
            if (Objects.nonNull(versionField)) {
                sql.append(SEPARATOR)
                        .append(wrapColumn(versionField.getColumnName()))
                        .append(EQUAL)
                        .append(wrapColumn(versionField.getColumnName()))
                        .append(" +1 ");
            }
            sql.append(DeleteSqlBuilder.this.getWhereSqlBuilder().getSql());
            return sql.toString();
        }
    }
}
