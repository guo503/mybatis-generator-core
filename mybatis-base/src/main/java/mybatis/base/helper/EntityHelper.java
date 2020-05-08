package mybatis.base.helper;


import mybatis.base.exception.EntityUnDefinedException;
import mybatis.base.exception.ParamErrorException;
import mybatis.base.meta.EntityField;
import mybatis.base.meta.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityHelper {

    private static final Logger logger = LoggerFactory.getLogger(EntityHelper.class);

    private EntityInfo entityInfo;

    private Object entity;

    public EntityHelper(EntityInfo entityInfo, Object entity) {
        this.entityInfo = entityInfo;
        if (!this.entityInfo.getEntityClass().equals(entity.getClass())) {
            throw new EntityUnDefinedException("entity 的类型为" + entity.getClass() + "，但是entityInfo 里面的信息为" + entityInfo.getEntityClass());
        }
        this.entity = entity;
    }

    public Object getFieldValue(String fieldName) throws IllegalAccessException {
        EntityField entityField = this.entityInfo.getFieldNameMap().get(fieldName);
        if (Objects.isNull(entityField)) {
            throw new ParamErrorException("没有这个字段:" + fieldName);
        }
        return getFieldValue(entityField);
    }

    public Object getFieldValue(EntityField entityField) throws IllegalAccessException {
        if (!entityField.getField().isAccessible()) {
            entityField.getField().setAccessible(true);
        }
        return entityField.getField().get(this.entity);
    }

    public List<EntityField> listNotNullFieldList() {
        List<EntityField> entityFields = new ArrayList<>();
        try {
            for (EntityField entityField : this.entityInfo.getEntityFields()) {
                if (Objects.nonNull(getFieldValue(entityField))) {
                    entityFields.add(entityField);
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("获取成员属性对象失败", e);
        }
        return entityFields;
    }
}
