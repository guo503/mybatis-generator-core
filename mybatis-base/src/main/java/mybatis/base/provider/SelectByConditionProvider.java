package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;
import mybatis.core.entity.Condition;

public class SelectByConditionProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        Condition condition = getConditionParam(paramProviderContext.getParameters());
        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .fields(condition.getFieldsNames())
                .excludeFields(condition.getExcludeFieldsNames())
                .where(condition)
                .limit(condition.getLimitCondition())
                .forceMaster(condition.isforceMaster())
                .getSql();
    }

}
