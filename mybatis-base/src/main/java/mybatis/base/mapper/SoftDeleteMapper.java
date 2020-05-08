package mybatis.base.mapper;


import org.apache.ibatis.annotations.Param;
import mybatis.base.provider.LogicDeleteByPrimaryKeyProvider;
import mybatis.base.provider.anno.DeleteSqlProvider;
import mybatis.core.entity.ParamConstant;
import mybatis.core.mapper.BaseMapper;

public interface SoftDeleteMapper<T> extends BaseMapper<T> {

    /**
     * 逻辑删除
     *
     * @param t
     * @return
     */
    @DeleteSqlProvider(provider = LogicDeleteByPrimaryKeyProvider.class)
    int softDeletex(@Param(ParamConstant.PRIMARY_KEY) Object t);

}
