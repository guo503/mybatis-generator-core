package mybatis.base.template.bs.business;


import mybatis.core.entity.Condition;

import java.util.List;

/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public interface IBusiness<T, Q, R> {

    /**
     * 查询
     *
     * @param id id
     * @return CountryCode
     * @author guos
     * @date 2020/06/20 15:12
     */
    R get(Integer id);


    /**
     * 保存
     *
     * @param r r
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    int save(R r);


    /**
     * 更新
     *
     * @param r r
     * @return
     * @author guos
     * @date 2020/6/29 16:01
     **/
    int update(R r);


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
    List<R> listByCondition(Q q, int pageNum, int pageSize);


    /**
     * 根据条件类查询总数
     *
     * @param q q
     * @return
     * @author guos
     * @date 2020/6/29 20:11
     **/
    int countByCondition(Q q);



    /**
     * 根据已有condition查询
     * @param condition
     * @author guos
     * @date 2020/7/28 18:37
     * @return
     **/
    List<R> listByCondition(Condition<T> condition);


    /**
     * 根据已有condition统计
     * @param condition
     * @author guos
     * @date 2020/7/28 18:38
     * @return
     **/
    int countByCondition(Condition<T> condition);


    /**
     * 分批查询
     *
     * @param condition 查询条件类
     * @return
     * @author guos
     * @date 2020/6/30 20:22
     **/
    void doBatch(Condition<T> condition);
}
