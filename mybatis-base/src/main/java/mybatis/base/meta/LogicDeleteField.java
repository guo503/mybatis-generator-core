package mybatis.base.meta;


/**
 * 逻辑删除字段
 *
 * @author lgt
 * @date 2019/4/30 : 2:01 PM
 */
public class LogicDeleteField extends EntityField {


    private int isDelete;

    private int isNotDelete;

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getIsNotDelete() {
        return isNotDelete;
    }

    public void setIsNotDelete(int isNotDelete) {
        this.isNotDelete = isNotDelete;
    }
}
