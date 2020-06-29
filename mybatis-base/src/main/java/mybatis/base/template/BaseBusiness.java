package mybatis.base.template;


/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public interface BaseBusiness<T> {

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
}
