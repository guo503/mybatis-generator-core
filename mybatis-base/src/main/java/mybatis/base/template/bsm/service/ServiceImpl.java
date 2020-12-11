package mybatis.base.template.bsm.service;


import mybatis.base.template.bsm.manage.IManage;
import mybatis.core.entity.Condition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public class ServiceImpl<M extends IManage<T>, T> implements IService<T> {

    @Autowired
    protected M baseManage;

    /**
     * 查询
     *
     * @param id id
     * @return t
     * @author guos
     * @date 2020/06/20 15:12
     */
    @Override
    public T get(Integer id) {
        return baseManage.get(id);
    }


    /**
     * 查询
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    @Override
    public T getOne(T t) {
        return baseManage.getOne(t);
    }


    /**
     * 保存
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    @Override
    public int save(T t) {
        return baseManage.save(t);
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
    public T saveAndGet(T t) {
        return baseManage.saveAndGet(t);
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
        return baseManage.update(t);
    }


    /**
     * 根据条件查询id集合
     *
     * @param condition
     * @return
     * @author guos
     * @date 2020/7/1 9:47
     **/
    @Override
    public List<Integer> listId(Condition<T> condition) {
        return baseManage.listId(condition);
    }


    /**
     * 根据po查询列表
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/7/1 9:27
     **/
    @Override
    public List<T> list(T t) {
        return baseManage.list(t);
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
        return baseManage.list(t, pageNum, pageSize);
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
        return baseManage.count(t);
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
        return baseManage.listByCondition(condition);
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
        return baseManage.countByCondition(condition);
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
        return baseManage.listByIds(ids);
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
        return baseManage.listByIds(ids, maxSize);
    }


    /**
     * 根据ids查询map
     *
     * @param ids ids
     * @return
     * @author guos
     * @date 2020/7/1 10:12
     **/
    @Override
    public Map<Integer, T> mapByIds(List<Integer> ids) {
        return baseManage.mapByIds(ids);
    }

    /**
     * 根据条件查询map
     *
     * @param condition 条件
     * @return
     * @author guos
     * @date 2020/7/1 10:12
     **/
    @Override
    public Map<Integer, T> map(Condition<T> condition) {
        return baseManage.map(condition);
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
        return baseManage.batchList(gtId, condition);
    }
}
