package mybatis.base.meta;

import java.util.List;
import java.util.Map;

/**
 * @author lgt
 * @date 2019/4/28 : 9:01 PM
 */
public class EntityInfo {


    /**
     * 字段列表
     */
    private List<EntityField> entityFields;

    /**
     * 字段名字段map
     */
    private Map<String,EntityField> fieldNameMap;

    /**
     * 逻辑删除字段
     */
    private LogicDeleteField logicDeleteField;


    /**
     * 乐观锁控制
     */
    private VersionField versionField;

    /**
     * 主键字段
     */
    private PrimaryKeyField primaryKey;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 实体类型
     */
    private Class<?> entityClass;

    public List<EntityField> getEntityFields() {
        return entityFields;
    }

    public void setEntityFields(List<EntityField> entityFields) {
        this.entityFields = entityFields;
    }

    public LogicDeleteField getLogicDeleteField() {
        return logicDeleteField;
    }

    public void setLogicDeleteField(LogicDeleteField logicDeleteField) {
        this.logicDeleteField = logicDeleteField;
    }

    public VersionField getVersionField() {
        return versionField;
    }

    public void setVersionField(VersionField versionField) {
        this.versionField = versionField;
    }

    public PrimaryKeyField getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PrimaryKeyField primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Map<String, EntityField> getFieldNameMap() {
        return fieldNameMap;
    }

    public void setFieldNameMap(Map<String, EntityField> fieldNameMap) {
        this.fieldNameMap = fieldNameMap;
    }
}
