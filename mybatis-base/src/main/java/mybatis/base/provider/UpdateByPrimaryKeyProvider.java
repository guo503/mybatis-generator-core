package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;

/**
 * 根据
 *
 * @author lgt
 * @date 2019/5/20 : 9:25 AM
 */
public class UpdateByPrimaryKeyProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext context) {
        return SqlBuilder.update(context.getEntityInfo())
                .isSelective(false)
                .entity(getEntityParam(context.getParameters(), context.getEntityInfo().getEntityClass()))
                .wherePk()
                .getSql();
    }

}
