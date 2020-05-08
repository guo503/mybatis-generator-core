package mybatis.core.entity;

import java.io.Serializable;
import java.util.*;

/**
 * 查询条件
 *
 * @author lgt
 * @date 2019/5/16 : 1:39 PM
 */
public class Condition<T> implements Serializable {

    private static final long serialVersionUID = -804499068048856102L;

    private static final int DEFAULT_ROW = 10;

    private static final String AND = " AND ";

    private static final String OR = " OR ";

    private HashMap<String, Object> params;

    private List<Criteria> criteriaList;

    private List<OrderBy> orderByList;

    private Set<String> fieldsNames;

    private Set<String> excludeFieldsNames;

    private LimitCondition limitCondition;

    private boolean ignoreLogicDelete;

    private int paramIndex = 0;

    private boolean forceMaster;

    public Condition() {
        this.params = new HashMap<>();
        this.criteriaList = new ArrayList<>();
        this.orderByList = new ArrayList<>();
        this.limitCondition = new LimitCondition(0, DEFAULT_ROW);
    }

    public Set<String> getFieldsNames() {
        return fieldsNames;
    }

    public void setFieldsNames(String... fieldsNames) {
        this.fieldsNames = new HashSet<>(Arrays.asList(fieldsNames));
    }

    public Set<String> getExcludeFieldsNames() {
        return excludeFieldsNames;
    }

    public void setExcludeFieldsNames(String... excludeFieldsNames) {
        this.excludeFieldsNames = new HashSet<>(Arrays.asList(excludeFieldsNames));
    }

    public void ignoreLogicDelete(){
        this.ignoreLogicDelete = true;
    }

    public boolean isIgnoreLogicDelete(){
        return ignoreLogicDelete;
    }


    public void forceMaster(){
        this.forceMaster = true;
    }

    public boolean isforceMaster(){
        return this.forceMaster;
    }


    public Criteria createCriteria() {
        Criteria criteria = new Criteria();
        this.criteriaList.add(criteria);
        return criteria;
    }

    public Criteria orCriteria() {
        if (this.criteriaList.isEmpty()) {
            return createCriteria();
        }
        Criteria criteria = new Criteria(OR);
        this.criteriaList.add(criteria);
        return criteria;
    }

    public Criteria andCriteria() {
        if (this.criteriaList.isEmpty()) {
            return createCriteria();
        }
        Criteria criteria = new Criteria(AND);
        this.criteriaList.add(criteria);
        return criteria;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    /**
     * 默认是正序
     *
     * @param field 字段名
     * @return 排序条件
     */
    public OrderCriteria setOrderBy(String field) {
        return new OrderCriteria(field);
    }

    public void limit(int num, int row) {
        this.limitCondition = new PageCondition(row, num);
    }

    public void limit(int row) {
        this.limitCondition = new PageCondition(row, 1);
    }

    public LimitCondition getLimitCondition() {
        return limitCondition;
    }

    public boolean isHasCondition() {
        return !criteriaList.isEmpty();
    }

    private int generateParamIndex() {
        return paramIndex++;
    }

    private void addParam(String paramPlaceN, Object paramV) {
        this.params.put(paramPlaceN, paramV);
    }

    public class OrderCriteria implements Serializable {

        private static final long serialVersionUID = 7279799697254129137L;
        private OrderBy orderBy;

        private OrderCriteria(String field) {
            this.orderBy = new OrderBy(field);
            orderByList.add(orderBy);
        }

        /**
         * 默认是正序
         *
         * @param field 字段名
         * @return 排序条件
         */
        public OrderCriteria andOrderBy(String field) {
            return new OrderCriteria(field);
        }

        public OrderCriteria desc() {
            orderBy.desc();
            return this;
        }

    }

    /**
     * 条件
     */
    public class Criteria implements Serializable {

        private static final long serialVersionUID = 5527463636589920046L;
        private String criRel;

        private int index;

        private int paramIndex;

        private List<ParamCondition> paramConditions;

        private Criteria() {
            this.index = generateParamIndex();
            this.paramConditions = new ArrayList<>();
        }

        private Criteria(String criRel) {
            this.criRel = criRel;
            this.index = generateParamIndex();
            this.paramConditions = new ArrayList<>();
        }

        public Criteria andEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.EQUAL);
            return this;
        }

        public Criteria andNotEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.NOT_EQUAL);
            return this;
        }

        public Criteria andIn(String paramN, Collection collection) {
            addParamCondition(paramN, collection, ParamSymbol.IN);
            return this;
        }

        public Criteria andNotIn(String paramN, Collection collection) {
            addParamCondition(paramN, collection, ParamSymbol.NOT_IN);
            return this;
        }

        public Criteria andIsNull(String paramN) {
            addParamCondition(paramN, ParamSymbol.IS_NULL);
            return this;
        }

        public Criteria andIsNotNull(String paramN) {
            addParamCondition(paramN, ParamSymbol.IS_NOT_NULL);
            return this;
        }

        public Criteria andLike(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.LIKE);
            return this;
        }

        public Criteria andLessThan(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.LT);
            return this;
        }

        public Criteria andLessThanEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.LTE);
            return this;
        }

        public Criteria andGreaterThan(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.GT);
            return this;
        }

        public Criteria andGreaterThanEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.GTE);
            return this;
        }

        private void addParamCondition(String paramN, Object paramV, ParamSymbol symbol) {
            if (Objects.isNull(paramV)) {
                return;
            }
            ParamCondition paramCondition = new ParamCondition(this.index, paramIndex++, paramN, symbol);
            paramConditions.add(paramCondition);
            Condition.this.addParam(paramCondition.getParamName(), paramV);
        }

        private void addParamCondition(String paramN, ParamSymbol symbol) {
            paramConditions.add(new ParamCondition(this.index, paramIndex++, paramN, symbol));

        }

        public String getCriRel() {
            return criRel;
        }

        public int getIndex() {
            return index;
        }

        public List<ParamCondition> getParamConditions() {
            return paramConditions;
        }
    }

}
