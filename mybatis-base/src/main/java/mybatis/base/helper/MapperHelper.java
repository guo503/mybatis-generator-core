package mybatis.base.helper;


import mybatis.base.meta.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mapper
 * @author lgt
 * @date 2019/5/2 : 2:34 PM
 */
public class MapperHelper {

    private static final Map<Class<?>, EntityInfo> MAPPER_ENTITY_INFO = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(MapperHelper.class);

    /**
     *
     * @param mapper
     * @return
     */
    public static EntityInfo addMappingEntity(Class<?> mapper) {
        logger.info("addMapping :{}", mapper.getName());

        Class<?> entityClass = parseEntityClass(mapper);
        return getEntityInfo(entityClass);
    }

    public static EntityInfo getEntityInfo(Class<?> entityClass) {
        EntityInfo entityInfo = MAPPER_ENTITY_INFO.get(entityClass);
        if (Objects.isNull(entityInfo)) {
            entityInfo = TableParser.parse(entityClass);
            MAPPER_ENTITY_INFO.put(entityClass, entityInfo);
        }
        return entityInfo;
    }

    /**
     * 解析继承接口泛型中的类
     *
     * @param mapper mapper接口
     * @return entity类型
     */
    private static Class<?> parseEntityClass(Class<?> mapper) {
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) mapper.getGenericInterfaces()[0];
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }


}
