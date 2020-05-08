package mybatis.base.provider;


import mybatis.base.sql.builder.SqlBuilder;

public class LogicDeleteByPrimaryKeyProvider extends BaseProvider {
    @Override
    public String produce(ParamProviderContext paramProviderContext) {

        return SqlBuilder.delete(paramProviderContext.getEntityInfo())
                .wherePk()
                .logicDelete()
                .getSql();
    }
}
