package mybatis.base.mapper;


import org.apache.ibatis.annotations.Param;
import mybatis.base.provider.DeleteByPrimaryKeyProvider;
import mybatis.base.provider.anno.DeleteSqlProvider;
import mybatis.core.entity.ParamConstant;
import mybatis.core.mapper.BaseMapper;

/**
 * 删除mapper
 *
 * @author lgt
 * @date 2019/5/22 : 10:04 AM
 */
public interface DeleteMapper<T> extends BaseMapper<T> {

    /**
     * 物理删除
     *
     * @param t 主键
     * @return 删除结果
     */
    @DeleteSqlProvider(provider = DeleteByPrimaryKeyProvider.class)
    int realDeletex(@Param(ParamConstant.PRIMARY_KEY) Object t);

}
