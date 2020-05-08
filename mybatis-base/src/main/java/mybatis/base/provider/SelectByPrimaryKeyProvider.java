package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;

/**
 * 根据主键查询
 *
 * @author lgt
 * @date 2019/5/2 : 10:37 AM
 */
public class SelectByPrimaryKeyProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {


        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .wherePk()
                .getSql();
    }



}
