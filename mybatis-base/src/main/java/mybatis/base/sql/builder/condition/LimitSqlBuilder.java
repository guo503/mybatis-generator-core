package mybatis.base.sql.builder.condition;


import mybatis.base.meta.EntityInfo;
import mybatis.base.sql.builder.CommonBuilder;
import mybatis.core.entity.LimitCondition;

public class LimitSqlBuilder extends CommonBuilder {

    private LimitCondition limitCondition;

    protected LimitSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public LimitSqlBuilder(EntityInfo entityInfo, LimitCondition limitCondition) {
        super(entityInfo);
        this.limitCondition = limitCondition;
    }

    @Override
    public String getSql() {
        return LIMIT +
                this.limitCondition.getOffset() +
                SEPARATOR +
                this.limitCondition.getLimit();
    }
}
