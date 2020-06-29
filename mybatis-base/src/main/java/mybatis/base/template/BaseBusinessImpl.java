package mybatis.base.template;


import mybatis.base.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author guos
 * @date 2020/6/29 15:35
 **/
public class BaseBusinessImpl<M extends Mapper<T>, T> implements BaseBusiness<T> {

    @Autowired
    protected M baseMapper;

    /**
     * 查询
     *
     * @param id id
     * @return CountryCode
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
}
