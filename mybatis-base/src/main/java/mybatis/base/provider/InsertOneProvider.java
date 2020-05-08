package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;

/**
 * 插入单个
 *
 * @author lgt
 * @date 2019/5/22 : 9:35 AM
 */
public class InsertOneProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        return SqlBuilder.insert(paramProviderContext.getEntityInfo())
                .entity(getEntityParam(paramProviderContext.getParameters(), paramProviderContext.getEntityInfo().getEntityClass()))
                .isSelective(false)
                .getSql();
    }
}
