package mybatis.spring.dao.rowmapper;


import mybatis.base.helper.MapperHelper;
import mybatis.base.meta.EntityInfo;

import java.util.HashMap;
import java.util.Objects;

public class RowMapperStorage {

    private final HashMap<Class, EntityBeanRowMapper> ENTITY_BEAN_ROW_MAPPER_STORAGE = new HashMap<>();

    public <T> EntityBeanRowMapper<T>  getEntityRowMapper(Class<T> tClass){
        EntityBeanRowMapper<T> rowMapper = (EntityBeanRowMapper<T>) ENTITY_BEAN_ROW_MAPPER_STORAGE.get(tClass);
        if (Objects.isNull(rowMapper)) {
            EntityInfo entityInfo = MapperHelper.getEntityInfo(tClass);
            rowMapper = new EntityBeanRowMapper<>(entityInfo);
            ENTITY_BEAN_ROW_MAPPER_STORAGE.put(tClass, rowMapper);
        }
        return rowMapper;
    }



}
