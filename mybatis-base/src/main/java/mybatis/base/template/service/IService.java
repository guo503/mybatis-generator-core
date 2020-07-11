package mybatis.base.template.service;


import mybatis.core.entity.Condition;

import java.util.List;
import java.util.Map;

/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public interface IService<T> {

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
     * 根据条件查询id集合
     *
     * @param condition
     * @return
     * @author guos
     * @date 2020/7/1 9:47
     **/
    List<Integer> listId(Condition<T> condition);


    /**
     * 根据po查询列表
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/7/1 9:27
     **/
    List<T> list(T t);


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
    List<T> list(T t, int pageNum, int pageSize);


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
     * 根据ids查询map
     *
     * @param ids ids
     * @return
     * @author guos
     * @date 2020/7/1 10:12
     **/
    Map<Integer, T> mapByIds(List<Integer> ids);


    /**
     * 根据条件查询map
     *
     * @param condition 条件
     * @return
     * @author guos
     * @date 2020/7/1 10:12
     **/
    Map<Integer, T> map(Condition<T> condition);

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
}
