package mybatis.base.mapper;


import org.apache.ibatis.annotations.Param;
import mybatis.base.provider.UpdateByPrimaryKeySelectiveProvider;
import mybatis.base.provider.anno.UpdateSqlProvider;
import mybatis.core.entity.ParamConstant;
import mybatis.core.mapper.BaseMapper;

/**
 * 更新
 *
 * @author lgt
 * @date 2019/6/3 : 3:26 PM
 */
public interface UpdateMapper<T> extends BaseMapper<T> {
//    /**
//     * 根据主键更新
//     *
//     * @param t 实体对象
//     * @return 更新数量
//     */PageSelectMapper
//    @UpdateSqlProvider(provider = UpdateByPrimaryKeyProvider.class)
//    int updateByPrimaryKey(@Param(ParamConstant.ENTITY) T t);

    /**
     * 根据主键更新非空字段
     *
     * @param t 实体对象
     * @return 更新数量
     */
    @UpdateSqlProvider(provider = UpdateByPrimaryKeySelectiveProvider.class)
    int updatex(@Param(ParamConstant.ENTITY) T t);

//    /**
//     * 根据条件更新 非空字段
//     *
//     * @param condition 条件
//     * @param t         实体对象
//     * @return 更新数量
//     */
//    @UpdateSqlProvider(provider = UpdateByConditionSelectiveProvider.class)
//    int updateByConditionSelective(@Param(ParamConstant.CONDITION) Condition condition, @Param(ParamConstant.ENTITY) T t);
//
//    /**
//     * 根据条件更新
//     *
//     * @param condition 条件
//     * @param t         实体对象
//     * @return 更新数量
//     */
//    @UpdateSqlProvider(provider = UpdateByConditionProvider.class)
//    int updateByCondition(@Param(ParamConstant.CONDITION) Condition condition, @Param(ParamConstant.ENTITY) T t);

}
