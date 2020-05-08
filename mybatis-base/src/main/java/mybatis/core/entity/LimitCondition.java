package mybatis.core.entity;

import java.io.Serializable;

public class LimitCondition implements Serializable {

    private static final int NO_ROW_OFFSET = 0;
    private static final int NO_ROW_LIMIT = Integer.MAX_VALUE;

    private static final long serialVersionUID = -6056036724118240187L;

    private final int offset;
    private final int limit;

    public LimitCondition() {
        this.offset = NO_ROW_OFFSET;
        this.limit = NO_ROW_LIMIT;
    }

    public LimitCondition(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
