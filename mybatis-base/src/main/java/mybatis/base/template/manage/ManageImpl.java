package mybatis.base.template.manage;


import com.google.common.collect.Lists;
import mybatis.base.helper.TableParser;
import mybatis.base.mapper.Mapper;
import mybatis.core.entity.Condition;
import mybatis.core.entity.LimitCondition;
import mybatis.core.page.PageInfo;
import mybatis.core.utils.ReflectionKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public class ManageImpl<M extends Mapper<T>, T> implements IManage<T> {

    @Autowired
    protected M baseMapper;

    /**
     * 查询
     *
     * @param id id
     * @return t
     * @author guos
     * @date 2020/06/20 15:12
     */
    public T get(Integer id) {
        Assert.notNull(id, "id不能为空");
        return baseMapper.getx(id);
    }


    /**
     * 查询
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    public T getOne(T t) {
        Assert.notNull(t, t + "不能为空");
        return baseMapper.getOnex(t);
    }


    /**
     * 保存
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    public int save(T t) {
        Assert.notNull(t, t + "不能为空");
        return baseMapper.savex(t);
    }

    /**
     * 新增并返回
     *
     * @param t
     * @return
     * @author guos
     * @date 2020/6/30 17:49
     **/
    @Override
    @Transactional
    public T saveAndGet(T t) {
        this.save(t);
        Class<?> entityClass = t.getClass();
        String primaryKeyName = TableParser.getPrimaryKeyName(entityClass);
        Object idVal = ReflectionKit.getMethodValue(entityClass, t, primaryKeyName);
        return this.get((Integer) idVal);
    }

    /**
     * 更新
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    @Override
    public int update(T t) {
        Assert.notNull(t, t + "不能为空");
        return baseMapper.updatex(t);
    }


    /**
     * 根据po查询列表
     *
     * @param t        t
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return
     * @author guos
     * @date 2020/7/1 9:27
     **/
    @Override
    public List<T> list(T t, int pageNum, int pageSize) {
        Assert.notNull(t, t + "不能为空");
        PageInfo<T> pageInfo = new PageInfo<>(pageSize, pageNum);
        return baseMapper.listLimitx(t, new LimitCondition(pageInfo.getOffset(), pageSize));
    }

    /**
     * 根据po查询总数
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/30 20:08
     **/
    @Override
    public int count(T t) {
        Assert.notNull(t, t + "不能为空");
        return baseMapper.countx(t);
    }

    /**
     * 根据条件类查询列表
     *
     * @param condition condition
     * @return
     * @author guos
     * @date 2020/6/29 20:11
     **/
    @Override
    public List<T> listByCondition(Condition<T> condition) {
        Assert.notNull(condition, "condition不能为空");
        return baseMapper.listByConditionx(condition);
    }

    /**
     * 根据条件类查询总数
     *
     * @param condition condition
     * @return
     * @author guos
     * @date 2020/6/29 20:11
     **/
    @Override
    public int countByCondition(Condition<T> condition) {
        Assert.notNull(condition, "condition不能为空");
        return baseMapper.countByConditionx(condition);
    }


    /**
     * 根据ids查询列表
     *
     * @param ids ids
     * @return
     * @author guos
     * @date 2020/6/30 20:59
     **/
    @Override
    public List<T> listByIds(List<Integer> ids) {
        return this.listByIds(ids, Integer.MAX_VALUE);
    }


    /**
     * 根据ids查询列表
     *
     * @param ids     ids
     * @param maxSize 最大查询数量
     * @return
     * @author guos
     * @date 2020/6/30 20:59
     **/
    @Override
    public List<T> listByIds(List<Integer> ids, int maxSize) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        Condition<T> condition = new Condition<>();
        T t = TableParser.getInstance(condition);
        if (Objects.isNull(t)) {
            throw new RuntimeException("获取condition实例失败");
        }
        String primaryKeyName = TableParser.getPrimaryKeyName(t.getClass());
        condition.createCriteria().andIn(primaryKeyName, ids);
        condition.limit(maxSize);
        return this.listByCondition(condition);
    }

    /**
     * 分批查询
     *
     * @param gtId      每批最大id
     * @param condition 查询条件
     * @return
     * @author guos
     * @date 2020/6/30 20:22
     **/
    @Override
    public List<T> batchList(int gtId, Condition<T> condition) {
        return this.batchList(gtId, condition, Integer.MAX_VALUE);
    }


    /**
     * 分批查询
     *
     * @param gtId         每批最大id
     * @param condition    查询条件
     * @param maxBatchSize 每批最大查询数量
     * @return
     * @author guos
     * @date 2020/6/30 20:22
     **/
    @Override
    public List<T> batchList(int gtId, Condition<T> condition, int maxBatchSize) {
        Assert.notNull(condition, condition + "不能为空");
        T t = TableParser.getInstance(condition);
        if (Objects.isNull(t)) {
            throw new RuntimeException("获取condition实例失败");
        }
        String primaryKeyName = TableParser.getPrimaryKeyName(t.getClass());
        //每次最多查询2000条数据
        condition.limit(1, maxBatchSize);
        condition.setOrderBy(primaryKeyName);
        condition.andCriteria().andGreaterThan(primaryKeyName, gtId);
        return this.listByCondition(condition);
    }
}
