package mybatis.base.template.manage;


import mybatis.core.entity.Condition;

import java.util.List;

/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public interface IManage<T> {

    /**
     * 查询
     *
     * @param id id
     * @return CountryCode
     * @author guos
     * @date 2020/06/20 15:12
     */
    T get(Integer id);


    /**
     * 查询
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    T getOne(T t);


    /**
     * 保存
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    int save(T t);

    /**
     * 新增并返回
     *
     * @param t
     * @return
     * @author guos
     * @date 2020/6/30 17:49
     **/
    T saveAndGet(T t);

    /**
     * 更新
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    int update(T t);


    /**
     * 根据po查询列表
     *
     * @param t
     * @return
     * @author guos
     * @date 2020/6/30 20:08
     **/
    List<T> list(T t);


    /**
     * 根据po查询总数
     *
     * @param t
     * @return
     * @author guos
     * @date 2020/6/30 20:08
     **/
    int count(T t);


    /**
     * 根据条件类查询列表
     *
     * @param condition condition
     * @return
     * @author guos
     * @date 2020/6/29 20:11
     **/
    List<T> listByCondition(Condition<T> condition);


    /**
     * 根据条件类查询总数
     *
     * @param condition condition
     * @return
     * @author guos
     * @date 2020/6/29 20:11
     **/
    int countByCondition(Condition<T> condition);


    /**
     * 根据ids查询列表
     *
     * @param ids ids
     * @return
     * @author guos
     * @date 2020/6/30 20:59
     **/
    List<T> listByIds(List<Integer> ids);



    /**
     * 根据ids查询列表
     *
     * @param ids     ids
     * @param maxSize 最大查询数量
     * @return
     * @author guos
     * @date 2020/6/30 20:59
     **/
    List<T> listByIds(List<Integer> ids, int maxSize);

    /**
     * 分批查询
     *
     * @param gtId      每批最大id
     * @param condition 查询条件
     * @return
     * @author guos
     * @date 2020/6/30 20:22
     **/
    List<T> batchList(int gtId, Condition<T> condition);

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
    List<T> batchList(int gtId, Condition<T> condition, int maxBatchSize);
}
