package mybatis.base.sql.builder.condition;


import mybatis.base.exception.ColumnUnknowException;
import mybatis.base.meta.EntityInfo;
import mybatis.base.meta.ParamValue;
import mybatis.base.meta.PrimaryKeyField;
import mybatis.core.entity.ParamConstant;
import mybatis.core.entity.ParamSymbol;

import java.util.Objects;

public class PrimaryKeySqlBuilder extends WhereSqlBuilder {

    private String replacePlace;

    public PrimaryKeySqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public PrimaryKeySqlBuilder(EntityInfo entityInfo, String replacePlace) {
        super(entityInfo);
        this.replacePlace = replacePlace;
    }

    @Override
    protected StringBuilder getWhereSql() {
        StringBuilder stringBuilder = new StringBuilder();
        replacePlace = hasLength(replacePlace) ? replacePlace : ParamConstant.PRIMARY_KEY;
        PrimaryKeyField primaryKeyField = getEntityInfo().getPrimaryKey();
        if (Objects.isNull(primaryKeyField)) {
            throw new ColumnUnknowException("entity 没有定义主键。");
        }
        stringBuilder.append(getParam(new ParamValue(primaryKeyField.getColumnName(), ParamSymbol.EQUAL, replacePlace)));
        this.setHasCondition(true);

        return stringBuilder;
    }
}
