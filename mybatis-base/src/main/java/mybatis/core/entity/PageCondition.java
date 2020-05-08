package mybatis.core.entity;

public class PageCondition extends LimitCondition {
    /**
     * 页数
     */
    private int num = 1;
    /**
     * 每页数量
     */
    private int row = 10;

    private static final Integer MAX_ROW = 2000;

    public PageCondition(int row, int num) {
        this.num = num;
        this.row = row;
    }

    public PageCondition() {
    }

    @Override
    public int getOffset() {
        return this.num < 1 ? 1 : (this.num - 1) * this.row;
    }

    @Override
    public int getLimit() {
        return this.row >= MAX_ROW ? MAX_ROW : this.row;
    }
}
