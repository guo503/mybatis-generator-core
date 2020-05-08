package mybatis.base.provider;


import org.apache.ibatis.binding.MapperMethod;
import mybatis.base.sql.builder.SqlBuilder;
import mybatis.core.entity.LimitCondition;
import mybatis.core.entity.ParamConstant;

/**
 * 根据实体类查询
 *
 * @author lgt
 * @date 2019/5/2 : 3:46 PM
 */
public class SelectByEntityProvider extends BaseProvider {
    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        LimitCondition limitCondition = getLimitParam(paramProviderContext.getParameters());
        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .where(getEntityParam(paramProviderContext.getParameters(), paramProviderContext.getEntityInfo().getEntityClass()))
                .limit(limitCondition)
                .getSql();

    }

    private LimitCondition getLimitParam(Object params) {
        if (params instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) params;
            if (paramMap.containsKey(ParamConstant.LIMIT)) {
                Object limitObj = paramMap.get(ParamConstant.LIMIT);
                return limitObj instanceof LimitCondition ? (LimitCondition) limitObj : null;
            }
        }
        return null;
    }

}
