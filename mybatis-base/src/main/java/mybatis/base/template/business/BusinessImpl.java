package mybatis.base.template.business;


import com.google.common.collect.Lists;
import mybatis.base.helper.TableParser;
import mybatis.base.template.service.IService;
import mybatis.core.entity.Condition;
import mybatis.core.page.Page;
import mybatis.core.utils.StrUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public class BusinessImpl<S extends IService<T>, T, Q, R> implements IBusiness<T, Q, R> {

    @Autowired
    protected S baseService;


    /**
     * 查询
     *
     * @param id id
     * @return CountryCode
     * @author guos
     * @date 2020/06/20 15:12
     */
    @Override
    public R get(Integer id) {
        T t = baseService.get(id);
        R r = TableParser.getInstance(this.getClass(), 3);
        if (Objects.isNull(t)) {
            return r;
        }
        if (Objects.isNull(r)) {
            return null;
        }
        BeanUtils.copyProperties(t, r);
        return r;
    }

    /**
     * 保存
     *
     * @param r r
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    @Override
    public int save(R r) {
        T t = TableParser.getInstance(this.getClass(), 1);
        if (Objects.isNull(t)) {
            throw new RuntimeException("获取实体类失败!");
        }
        BeanUtils.copyProperties(r, t);
        return baseService.save(t);
    }

    /**
     * 更新
     *
     * @param r r
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    @Override
    public int update(R r) {
        T t = TableParser.getInstance(this.getClass(), 1);
        if (Objects.isNull(t)) {
            throw new RuntimeException("获取实体类失败!");
        }
        BeanUtils.copyProperties(r, t);
        return baseService.update(t);
    }

    /**
     * 根据条件类查询列表
     *
     * @param q        查询条件类
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return
     * @author guos
     * @date 2020/7/28 11:17
     **/
    @Override
    public List<R> listByCondition(Q q, int pageNum, int pageSize) {
        List<R> result = Lists.newArrayList();
        Condition<T> condition = this.getCondition(q);
        if (Objects.isNull(condition)) {
            return result;
        }
        condition.limit(pageNum, pageSize);
        List<T> tList = baseService.listByCondition(condition);
        if (CollectionUtils.isEmpty(tList)) {
            return result;
        }
        return this.listR(tList);
    }

    private List<R> listR(List<T> tList) {
        R r;
        List<R> result = Lists.newArrayList();
        for (T t : tList) {
            r = TableParser.getInstance(this.getClass(), 3);
            if (Objects.isNull(r)) {
                throw new RuntimeException("获取返回对象失败!");
            }
            BeanUtils.copyProperties(t, r);
            result.add(r);
        }
        return result;
    }

    /**
     * 根据条件类查询总数
     *
     * @param q 查询条件类
     * @return
     * @author guos
     * @date 2020/6/29 20:11
     **/
    @Override
    public int countByCondition(Q q) {
        Condition<T> condition = this.getCondition(q);
        if (Objects.isNull(condition)) {
            return 0;
        }
        return baseService.countByCondition(condition);
    }


    /**
     * 根据已有condition查询
     *
     * @param condition condition
     * @return
     * @author guos
     * @date 2020/7/28 18:37
     **/
    @Override
    public List<R> listByCondition(Condition<T> condition) {
        if (Objects.isNull(condition)) {
            return Lists.newArrayList();
        }
        //没有排序的话，默认按id升序
        if (CollectionUtils.isEmpty(condition.getOrderByList())) {
            T t = TableParser.getInstance(this.getClass(), 1);
            if (Objects.isNull(t)) {
                throw new RuntimeException("获取实体类失败!");
            }
            String primaryKeyName = TableParser.getPrimaryKeyName(t.getClass());
            condition.setOrderBy(primaryKeyName);
        }
        return this.listR(baseService.listByCondition(condition));
    }

    /**
     * 根据已有condition统计
     *
     * @param condition condition
     * @return
     * @author guos
     * @date 2020/7/28 18:38
     **/
    @Override
    public int countByCondition(Condition<T> condition) {
        if (Objects.isNull(condition)) {
            return 0;
        }
        return baseService.countByCondition(condition);
    }

    /**
     * 组装查询条件
     *
     * @return
     */
    protected Condition<T> getCondition(Q q) {
        if (Objects.isNull(q)) {
            return null;
        }
        T t = TableParser.getInstance(this.getClass(), 1);
        if (Objects.isNull(t)) {
            throw new RuntimeException("获取实体类失败!");
        }
        Condition<T> condition = new Condition<>();
        Condition<T>.Criteria criteria = condition.createCriteria();
        Field[] queryFields = q.getClass().getDeclaredFields();
        List<Field> fields = Lists.newArrayList(t.getClass().getDeclaredFields());
        Map<String, Field> entityMap = fields.stream().filter(f -> Objects.equals(f.getModifiers(), Modifier.PRIVATE)).collect(Collectors.toMap(Field::getName, Function.identity(), (k1, k2) -> k2));
        for (Field field : queryFields) {
            field.setAccessible(true); // 私有属性必须设置访问权限
            //设置查询条件
            this.setCriteria(criteria, q, entityMap, field);
        }
        return condition;
    }

    /**
     * 分批查询
     *
     * @param condition 查询条件类
     *                  每次最大查询数量
     * @return
     * @author guos
     * @date 2020/6/30 20:22
     **/
    @Override
    public void doBatch(Condition<T> condition) {
        if (Objects.isNull(condition)) {
            throw new RuntimeException("查询条件不能为空!");
        }
        int maxSize = Page.MAX_SIZE - 1;
        int size = maxSize;
        int gtId = 0;
        while (size >= maxSize) {
            List<T> list = baseService.batchList(gtId, condition);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            size = list.size();
            gtId = TableParser.getPrimaryKeyVal(list.get(size - 1));
        }
    }


    private void setCriteria(Condition<T>.Criteria criteria, Q q, Map<String, Field> entityMap, Field field) {
        String fieldName = field.getName();
        Class<?> type = field.getType();
        //集合
        boolean isList = Objects.equals(type, List.class);
        //不等于,小于等于,大于等于,模糊,非空,空,In,notIn
        //默认等于
        boolean isNeq, isLte, isGte, isLk, isNN, isN, isNin = false;
        isNeq = fieldName.startsWith("neq");
        isLte = fieldName.startsWith("lte");
        isGte = fieldName.startsWith("gte");
        isN = fieldName.startsWith("n");
        isNN = fieldName.startsWith("nn");
        isLk = fieldName.startsWith("lk");

        String actualName = fieldName;

        if (isList) {
            actualName = fieldName.substring(0, fieldName.lastIndexOf("s"));
            isNin = fieldName.startsWith("nin");
            if (isNin) {
                actualName = StrUtils.toLowerCaseFirst(actualName.substring(3));
            }
        } else if (isNeq || isLte || isGte) {
            actualName = StrUtils.toLowerCaseFirst(fieldName.substring(3));
        } else if (isLk || isNN) {
            actualName = StrUtils.toLowerCaseFirst(fieldName.substring(2));
        } else if (isN) {
            actualName = StrUtils.toLowerCaseFirst(fieldName.substring(1));
        }

        if (Objects.isNull(entityMap.get(actualName))) {
            if (Objects.isNull(entityMap.get(fieldName))) {
                return;
            }
        }
        Object fieldValue;
        try {
            fieldValue = field.get(q);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("获取属性" + field.getName() + "失败!");
        }
        if (Objects.isNull(fieldValue)) {
            return;
        }
        if (isList) {
            if (isNin) {
                criteria.andNotIn(actualName, (List) fieldValue);
            } else {
                criteria.andIn(actualName, (List) fieldValue);
            }
        } else if (isNeq) {
            criteria.andNotEqual(actualName, fieldValue);
        } else if (isLte) {
            criteria.andLessThanEqual(actualName, fieldValue);
        } else if (isGte) {
            criteria.andGreaterThanEqual(actualName, fieldValue);
        } else if (isLk) {
            criteria.andLike(actualName, "%" + fieldValue + "%");
        } else if (isNN) {
            criteria.andIsNotNull(actualName);
        } else if (isN) {
            criteria.andIsNull(actualName);
        } else {
            criteria.andEqual(actualName, fieldValue);
        }
    }


}
