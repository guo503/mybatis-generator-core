package mybatis.core.entity;


/**
 * 参数符号
 *
 * @author lgt
 * @date 2019/5/10 : 6:02 PM
 */
public enum ParamSymbol {
    /**
     * 等于
     */
    EQUAL("="),

    /**
     * 不等于
     */
    NOT_EQUAL("!="),
    /**
     * 小于
     */
    LT("<"),

    /**
     * 小于等于
     */
    LTE("<="),

    /**
     * 大于
     */
    GT(">"),

    /**
     * 大于
     */
    GTE(">="),

    /**
     * 包含
     */
    IN(" IN ", ParamConstant.PARAM_COLLECTION),

    /**
     * 不包含
     */
    NOT_IN(" NOT IN ", ParamConstant.PARAM_COLLECTION),

    /**
     * 模糊查询
     */
    LIKE(" LIKE "),
    /**
     * 为null
     */
    IS_NULL(" IS NULL ", ParamConstant.PARAM_NULL),

    /**
     * 不为空
     */
    IS_NOT_NULL(" IS NOT NULL ", ParamConstant.PARAM_NULL),

    ;

    String value;
    /**
     * 参数类型 0-是不需要参数，1-普通 2-数组
     */
    private int paramType;

    public String getValue() {
        return value;
    }

    public int getParamType() {
        return paramType;
    }

    ParamSymbol(String value) {
        this.value = value;
        this.paramType = ParamConstant.PARAM_SINGLE;
    }

    ParamSymbol(String value, int paramType) {
        this.value = value;
        this.paramType = paramType;
    }
}
