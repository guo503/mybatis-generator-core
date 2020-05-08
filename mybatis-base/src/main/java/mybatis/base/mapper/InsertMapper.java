package mybatis.base.mapper;


import org.apache.ibatis.annotations.Param;
import mybatis.base.provider.InsertBatchProvider;
import mybatis.base.provider.InsertOneSelectiveProvider;
import mybatis.base.provider.anno.InsertSqlProvider;
import mybatis.core.entity.ParamConstant;
import mybatis.core.mapper.BaseMapper;

import java.util.List;


public interface InsertMapper<T> extends BaseMapper<T> {

    @InsertSqlProvider(provider = InsertOneSelectiveProvider.class)
    int savex(T t);



    @InsertSqlProvider(provider = InsertBatchProvider.class)
    int saveBatchx(@Param(ParamConstant.ENTITY_LIST) List<T> t);


    //    @InsertSqlProvider(provider = InsertOneProvider.class)
//    int savex(T t);
}
