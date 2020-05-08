package mybatis.spring.dao.rowmapper;


import mybatis.base.meta.EntityField;
import mybatis.base.meta.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 实体对象映射类
 *
 * @author lgt
 * @date 2019/5/3 : 10:23 AM
 */
public class EntityBeanRowMapper<T> implements RowMapper<T> {




    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Class<T> mappedClass;
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();
    private Map<String, EntityField> mappedFields;


    private EntityInfo entityInfo;

    public EntityBeanRowMapper(EntityInfo entityInfo) {
        this.mappedClass = (Class<T>) entityInfo.getEntityClass();
        this.entityInfo = entityInfo;
        this.initialize(entityInfo);
    }


    public final Class<T> getMappedClass() {
        return this.mappedClass;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ConversionService getConversionService() {
        return this.conversionService;
    }

    protected void initialize(EntityInfo entityInfo) {
        this.mappedFields = new HashMap<>();
        List<EntityField> fieldList = entityInfo.getEntityFields();
        for (int i = 0; i < fieldList.size(); i++) {
            EntityField entityField = fieldList.get(i);
            mappedFields.put(entityField.getColumnName(), entityField);
        }

    }


    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        this.initBeanWrapper(bw);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int index = 1; index <= columnCount; ++index) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            EntityField entityField = mappedFields.get(column);
            if (entityField == null) {
                if (rowNumber == 0) {
                    this.logger.debug("No property found for column '" + column + "' mapped ");
                }
            } else {
                try {
                    Object value = this.getColumnValue(rs, index, entityField);
                    try {
                        bw.setPropertyValue(entityField.getFieldName(), value);
                    } catch (TypeMismatchException var14) {
                        this.logger.warn("Intercepted TypeMismatchException for row " + rowNumber + " and column '" + column + "' with null value when setting property '" + entityField.getFieldName() + "' of type '" + ClassUtils.getQualifiedName(entityField.getJavaType()) + "' on object: " + mappedObject, var14);
                    }
                } catch (NotWritablePropertyException var15) {
                    throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '" + entityField.getFieldName() + "'", var15);
                }
            }
        }
        return mappedObject;
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        ConversionService cs = this.getConversionService();
        if (cs != null) {
            bw.setConversionService(cs);
        }

    }

    protected Object getColumnValue(ResultSet rs, int index, EntityField field) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, field.getJavaType());
    }


}
