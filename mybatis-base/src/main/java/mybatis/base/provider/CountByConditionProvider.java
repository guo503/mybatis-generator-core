package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;

/**
 * 根据condition查询数量
 *
 * @author lgt
 * @date 2019/5/22 : 5:56 PM
 */
public class CountByConditionProvider extends BaseProvider {
    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .where(getConditionParam(paramProviderContext.getParameters()))
                .count()
                .getSql();
    }

}
