package mybatis.spring.dao.rowmapper;

public class ParamBoundSql {

    private Object[] params;

    private String sql;

    public ParamBoundSql(Object[] params, String sql) {
        this.params = params;
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
