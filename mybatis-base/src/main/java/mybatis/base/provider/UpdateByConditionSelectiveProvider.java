package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;
import mybatis.core.entity.Condition;

public class UpdateByConditionSelectiveProvider extends BaseProvider {
    @Override
    public String produce(ParamProviderContext context) {
        Condition condition = getConditionParam(context.getParameters());
        return SqlBuilder.update(context.getEntityInfo())
                .isSelective(true)
                .entity(getEntityParam(context.getParameters(), context.getEntityInfo().getEntityClass()))
                .where(getConditionParam(context.getParameters()))
                .fields(condition.getFieldsNames())
                .excludeFields(condition.getExcludeFieldsNames())
                .getSql();
    }

}
