package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;
import mybatis.core.entity.Condition;

public class UpdateByConditionProvider extends BaseProvider {


    @Override
    public String produce(ParamProviderContext context) {
        Condition condition = getConditionParam(context.getParameters());
        return SqlBuilder.update(context.getEntityInfo())
                .isSelective(false)
                .where(getConditionParam(context.getParameters()))
                .entity(getEntityParam(context.getParameters(), context.getEntityInfo().getEntityClass()))
                .fields(condition.getFieldsNames())
                .excludeFields(condition.getExcludeFieldsNames())
                .getSql();
    }


}
