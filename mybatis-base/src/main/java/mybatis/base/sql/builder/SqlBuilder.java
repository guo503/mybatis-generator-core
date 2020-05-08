package mybatis.base.sql.builder;


import mybatis.base.meta.EntityInfo;
import mybatis.base.sql.builder.condition.WhereSqlBuilder;

/**
 * @author lgt
 * @date 2019/5/10 : 3:17 PM
 */
public abstract class SqlBuilder extends CommonBuilder{

    private WhereSqlBuilder whereSqlBuilder;

    public WhereSqlBuilder getWhereSqlBuilder() {
        return whereSqlBuilder;
    }

    public void setWhereSqlBuilder(WhereSqlBuilder whereSqlBuilder) {
        this.whereSqlBuilder = whereSqlBuilder;
    }

    protected SqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public static SelectSqlBuilder select(EntityInfo entityInfo) {
        return new SelectSqlBuilder(entityInfo);
    }

    public static UpdateSqlBuilder update(EntityInfo entityInfo) {
        return new UpdateSqlBuilder(entityInfo);
    }

    public static InsertSqlBuilder insert(EntityInfo entityInfo) {
        return new InsertSqlBuilder(entityInfo);
    }


    public static DeleteSqlBuilder delete(EntityInfo entityInfo) {
        return new DeleteSqlBuilder(entityInfo);
    }


}
