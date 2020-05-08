package mybatis.base.provider;


import org.apache.ibatis.binding.MapperMethod;
import mybatis.base.sql.builder.SqlBuilder;
import mybatis.core.entity.ParamConstant;

import java.util.List;

/**
 * 批量插入
 *
 * @author lgt
 * @date 2019/5/21 : 8:37 PM
 */
public class InsertBatchProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        List list = getInsertList(paramProviderContext.getParameters());
        return SqlBuilder.insert(paramProviderContext.getEntityInfo())
                .entity(list)
                .isSelective(false)
                .getSql();
    }

    private List getInsertList(Object params) {
        if (params instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) params;
            Object entity = paramMap.get(ParamConstant.ENTITY_LIST);
            List list = entity instanceof List ? (List) entity : null;
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    paramMap.put(String.valueOf(i), list.get(i));
                }
            }
            return list;
        }
        if (params instanceof List){
            return  (List) params;
        }
        return null;
    }

}
