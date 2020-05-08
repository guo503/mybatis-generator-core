package mybatis.base.provider;


import org.apache.ibatis.binding.MapperMethod;
import mybatis.core.entity.Condition;
import mybatis.core.entity.ParamConstant;

/**
 * sql 提供基类
 *
 * @author lgt
 * @date 2019/4/29 : 6:57 PM
 */
public abstract class BaseProvider {

    /**
     * 创建sql
     *
     * @param paramProviderContext 创建sql的上下文
     * @return 返回sql的字符床
     */
    public abstract String produce(ParamProviderContext paramProviderContext);


    protected Condition getConditionParam(Object params) {
        if (params instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) params;
            Object condition = paramMap.get(ParamConstant.CONDITION);
            return condition instanceof Condition ? (Condition) condition : null;
        }
        if (params instanceof Condition) {
            return (Condition) params;
        }
        if (params.getClass().isArray()) {
            Object[] paramArray = (Object[]) params;
            for (Object param : paramArray) {
                if (param instanceof Condition) {
                    return (Condition) param;
                }
            }
        }
        return null;
    }

    protected Object getEntityParam(Object params , Class clazz) {
        if (params instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) params;
            return paramMap.get(ParamConstant.ENTITY);
        }
        if (params.getClass().isArray()) {
            Object[] paramArray = (Object[]) params;
            for (Object param : paramArray) {
                if (clazz.equals(param.getClass())) {
                    return param;
                }
            }
        }
        return params;
    }


}
