package mybatis.base.sql.builder;


import mybatis.base.exception.ColumnUnknowException;
import mybatis.base.meta.EntityField;
import mybatis.base.meta.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author lgt
 * @date 2019/5/12 : 9:57 PM
 */
public abstract class CommonBuilder {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String SELECT = "SELECT ";
    protected static final String INSERT = "INSERT INTO ";
    protected static final String VALUES = " VALUES ";

    protected static final String UPDATE = "UPDATE ";
    protected static final String SET = " SET ";

    protected static final String DELETE = "DELETE ";
    protected static final String FROM = " FROM ";
    protected static final String WHERE = " WHERE ";
    protected static final String ORDER = " ORDER BY ";
    protected static final String LIMIT = " LIMIT ";

    protected static final String DESC = " DESC ";
    protected static final String ASC = " ASC ";
    protected static final String EQUAL = " = ";
    protected static final String AND = " AND ";
    protected static final String REPLACE_PLACE = "#{%s} ";
    protected static final String SEPARATOR = ", ";
    protected static final String DOT = ".";

    protected static final String EMPTY_SQL = "";

    protected static final String WORD_SURROUND = "`";

    protected static final String OPEN_PLACE = "(";
    protected static final String CLOSED_PLACE = ")";

    protected static final String FORCE_MASTER = "/*FORCE_MASTER*/";

    private EntityInfo entityInfo;

    protected CommonBuilder(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    /**
     * 获取sql
     *
     * @return
     */
    public abstract String getSql();


    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    protected static boolean isNotEmpty(Collection collection) {
        return Objects.nonNull(collection) && !collection.isEmpty();
    }

    public static boolean isEmpty(Collection collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    protected String wrapColumn(String column) {
        return WORD_SURROUND + column + WORD_SURROUND;
    }

    protected static boolean hasLength(String name) {
        return Objects.nonNull(name) && name.length() > 0;
    }

    protected List<EntityField> getSelectiveField(Set<String> fieldsNames) {
        List<EntityField> entityFields = new ArrayList<>();
        Map<String, EntityField> fieldMap = getEntityInfo().getFieldNameMap();
        //校验是否存在该成员属性对应的成员变量
        for (String fieldName : fieldsNames) {
            EntityField entityField = fieldMap.get(fieldName);
            if (Objects.isNull(entityField)) {
                logger.error("根据属性名查询不到该字段 :{}", fieldName);
                throw new ColumnUnknowException("根据属性名查询不到该字段 :" + fieldName);
            }
            entityFields.add(entityField);
        }
        return entityFields;
    }

    protected List<EntityField> getSelectiveFieldExclude(Set<String> excludeFieldsNames) {
        List<EntityField> entityFields = new ArrayList<>();
        Map<String, EntityField> fieldMap = getEntityInfo().getFieldNameMap();
        //校验是否存在该成员属性对应的成员变量
        for (String fieldName : excludeFieldsNames) {
            if (!fieldMap.containsKey(fieldName)) {
                logger.error("根据属性名查询不到该字段 :{}", fieldName);
                throw new ColumnUnknowException("根据属性名查询不到该字段 :" + fieldName);
            }
        }
        for (EntityField entityField : this.getEntityInfo().getEntityFields()) {
            if (excludeFieldsNames.contains(entityField.getFieldName())) {
                continue;
            }
            entityFields.add(entityField);
        }
        return entityFields;
    }

}
