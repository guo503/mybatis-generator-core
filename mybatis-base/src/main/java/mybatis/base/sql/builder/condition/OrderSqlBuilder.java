package mybatis.base.sql.builder.condition;


import mybatis.base.exception.ColumnUnknowException;
import mybatis.base.meta.EntityField;
import mybatis.base.meta.EntityInfo;
import mybatis.base.sql.builder.CommonBuilder;
import mybatis.core.entity.OrderBy;

import java.util.List;
import java.util.Objects;

public class OrderSqlBuilder extends CommonBuilder {

    private List<OrderBy> orderByList;

    public OrderSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public OrderSqlBuilder(EntityInfo entityInfo, List<OrderBy> orderByList) {
        super(entityInfo);
        this.orderByList = orderByList;
    }

    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    public void setOrderByList(List<OrderBy> orderByList) {
        this.orderByList = orderByList;
    }

    @Override
    public String getSql() {
        StringBuilder orderSql = new StringBuilder();
        if (isNotEmpty(orderByList)) {
            orderSql.append(ORDER);
            for (int i = 0; i < orderByList.size(); i++) {
                EntityField entityField = getEntityInfo().getFieldNameMap().get(orderByList.get(i).getField());
                if (Objects.isNull(entityField)) {
                    throw new ColumnUnknowException("属性" + orderByList.get(i).getField() + "没有对应字段");
                }
                orderSql.append(wrapColumn(entityField.getColumnName()));
                if (orderByList.get(i).isDesc()) {
                    orderSql.append(DESC);
                } else {
                    orderSql.append(ASC);
                }
                if (i != orderByList.size() - 1) {
                    orderSql.append(SEPARATOR);
                }
            }
        }
        return orderSql.toString();
    }

}
