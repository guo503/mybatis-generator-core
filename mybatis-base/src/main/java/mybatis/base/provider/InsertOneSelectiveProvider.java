package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;

/**
 * 单个插入
 *
 * @author lgt
 * @date 2019/5/21 : 8:37 PM
 */
public class InsertOneSelectiveProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        return SqlBuilder.insert(paramProviderContext.getEntityInfo())
                .entity(getEntityParam(paramProviderContext.getParameters(), paramProviderContext.getEntityInfo().getEntityClass()))
                .isSelective(true)
                .getSql();
    }
}
