package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;

/**
 * 根据实体类查询数量
 *
 * @author lgt
 * @date 2019/5/22 : 5:54 PM
 */
public class CountByEntityProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .where(getEntityParam(paramProviderContext.getParameters(), paramProviderContext.getEntityInfo().getEntityClass()))
                .count()
                .getSql();
    }
}
