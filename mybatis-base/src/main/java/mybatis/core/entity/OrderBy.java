package mybatis.core.entity;

import java.io.Serializable;

/**
 * 排序字段
 *
 * @author lgt
 * @date 2019/5/16 : 3:10 PM
 */
public class OrderBy implements Serializable {

    private static final long serialVersionUID = -5688082490166136317L;
    private String field;

    private boolean isDesc;

    OrderBy(String field) {
        this.field = field;
    }


    public void desc() {
        this.isDesc = true;
    }

    public String getField() {
        return field;
    }

    public boolean isDesc() {
        return isDesc;
    }
}
