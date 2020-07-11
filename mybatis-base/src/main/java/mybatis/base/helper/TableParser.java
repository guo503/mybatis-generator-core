package mybatis.base.helper;


import mybatis.base.exception.EntityUnDefinedException;
import mybatis.base.meta.*;
import mybatis.core.annotation.*;
import mybatis.core.utils.ReflectionKit;
import mybatis.core.utils.StrUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * 实体类解析成数据库表
 *
 * @author lgt
 * @date 2019/4/30 : 11:55 AM
 */
public class TableParser {


    public static EntityInfo parse(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (Objects.isNull(table)) {
            throw new EntityUnDefinedException("实体类没有包含Table注解");
        }
        if ("".equals(table.name().trim())) {
            throw new EntityUnDefinedException("Table注解没有包含有效的表名");
        }
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName(table.name());
        entityInfo.setEntityClass(entityClass);

        NameParser nameParser = ColumnNameParserFactory.getParser(table.style());
        Field[] fields = entityClass.getDeclaredFields();
        List<EntityField> fieldList = new ArrayList<>();
        Map<String, EntityField> fieldNameMap = new HashMap<>();
        for (Field field : fields) {
            //静态成员属性不需要解析
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //非数据库字段
            if (Objects.nonNull(field.getAnnotation(Transient.class))) {
                continue;
            }
            //解析主键
            Id id = field.getAnnotation(Id.class);
            LogicDelete logicDelete = field.getAnnotation(LogicDelete.class);
            Version version = field.getAnnotation(Version.class);
            Column column = field.getAnnotation(Column.class);
            EntityField entityField;
            if (Objects.nonNull(id)) {
                //这个是主键字段
                entityField = createPrimaryKeyField(entityInfo, id);
            } else if (Objects.nonNull(logicDelete)) {
                //字段为逻辑删除类型
                entityField = createLogicDeleteField(entityInfo, logicDelete, column);
            } else if (Objects.nonNull(version)) {
                //这是版本管控，乐观锁字段
                entityField = createVersionField(entityInfo, version, column);
            } else {
                entityField = new EntityField();
                if (Objects.nonNull(column)) {
                    entityField.setInsertable(column.insertable());
                    entityField.setUpdatable(column.updatable());
                    //没有指明字段名用属性名解析
                    if (hasLength(column.name())) {
                        entityField.setColumnName(column.name());
                    }
                }
            }
            entityField.setFieldName(field.getName());
            entityField.setField(field);
            //以上逻辑没有标注数据库字段名
            if (!hasLength(entityField.getColumnName())) {
                entityField.setColumnName(nameParser.parse(entityField.getFieldName()));
            }
            entityField.setJavaType(field.getType());
            fieldList.add(entityField);
            fieldNameMap.put(entityField.getFieldName(), entityField);
        }
        entityInfo.setEntityFields(fieldList);
        entityInfo.setFieldNameMap(fieldNameMap);
        return entityInfo;
    }

    private static LogicDeleteField createLogicDeleteField(EntityInfo entityInfo, LogicDelete logicDelete, Column column) {
        if (Objects.nonNull(entityInfo.getLogicDeleteField())) {
            throw new EntityUnDefinedException("一个实体只能有一个逻辑删除字段");
        }
        LogicDeleteField logicDeleteField = new LogicDeleteField();
        logicDeleteField.setIsDelete(logicDelete.isDelete());
        logicDeleteField.setIsNotDelete(logicDelete.isNotDelete());
        if (Objects.nonNull(column)) {
            //TODO 乐观锁字段的 是否可新增，是否可删除
            logicDeleteField.setUpdatable(column.updatable());
            //没有指明字段名用属性名解析
            if (hasLength(column.name())) {
                logicDeleteField.setColumnName(column.name());
            }
        }
        entityInfo.setLogicDeleteField(logicDeleteField);
        return logicDeleteField;
    }

    private static VersionField createVersionField(EntityInfo entityInfo, Version version, Column column) {
        //字段为版本类型
        if (Objects.nonNull(entityInfo.getVersionField())) {
            throw new EntityUnDefinedException("一个实体只能有一个版本号字段");
        }
        VersionField versionField = new VersionField();
        //TODO 乐观锁字段的 是否可新增，是否可删除
        versionField.setInsertable(false);
        versionField.setUpdatable(false);
        if (Objects.nonNull(column)) {
            //没有指明字段名用属性名解析
            if (hasLength(column.name())) {
                versionField.setColumnName(column.name());
            }
        }
        entityInfo.setVersionField(versionField);
        return versionField;
    }

    private static PrimaryKeyField createPrimaryKeyField(EntityInfo entityInfo, Id id) {
        //判断主键是否已经有字段
        if (Objects.nonNull(entityInfo.getPrimaryKey())) {
            throw new EntityUnDefinedException("实体类中只能存在一个字段为主键");
        }
        PrimaryKeyField primaryKeyField = new PrimaryKeyField();
        if (hasLength(id.name())) {
            primaryKeyField.setColumnName(id.name());
        }
        primaryKeyField.setInsertable(false);
        primaryKeyField.setUpdatable(false);
        if (id.strategy().equals(GenerationType.PROVIDED)) {
            //如果主键是提供的，这设置主键可以新增
            primaryKeyField.setInsertable(true);
        }
        entityInfo.setPrimaryKey(primaryKeyField);
        return primaryKeyField;
    }


    private static boolean hasLength(String name) {
        return Objects.nonNull(name) && name.length() > 0;
    }


    public static String getPrimaryKeyName(Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        String primaryKeyName = null;
        for (Field field : fields) {
            //解析主键
            Id id = field.getAnnotation(Id.class);
            if (Objects.nonNull(id)) {
                primaryKeyName = StrUtils.isEmpty(id.name()) ? field.getName() : id.name();
                break;
            }
        }
        if (StrUtils.isEmpty(primaryKeyName)) {
            throw new RuntimeException("表必须要有主键!");
        }
        return primaryKeyName;
    }


    public static String getPrimaryKeyType(Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        String typeName = null;
        for (Field field : fields) {
            //解析主键
            Id id = field.getAnnotation(Id.class);
            if (Objects.nonNull(id)) {
                typeName = field.getType().getSimpleName();
                break;
            }
        }
        if (StrUtils.isEmpty(typeName)) {
            throw new RuntimeException("主键类型异常!");
        }
        return typeName;
    }


    public static <T> T getInstance(Object o) {
        try {
            Type type = o.getClass().getGenericSuperclass();
            if (type == null) {
                return null;
            }
            ParameterizedType parameterizedType = null;
            if (type instanceof ParameterizedType) {
                parameterizedType = (ParameterizedType) type;
            }
            if (parameterizedType == null) {
                return null;
            }
            Type[] types = parameterizedType.getActualTypeArguments();
            if (types.length == 0) {
                return null;
            }
            Type realType = parameterizedType.getActualTypeArguments()[0];
            Class<T> clz = (Class<T>) realType;
            return clz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据实例获取主键值
     *
     * @param t t
     * @return
     * @author guos
     * @date 2020/7/1 9:56
     **/
    public static <T> Integer getPrimaryKeyVal(T t) {
        Class<?> entityClass = t.getClass();
        String primaryKeyName = TableParser.getPrimaryKeyName(entityClass);
        return (Integer) ReflectionKit.getMethodValue(entityClass, t, primaryKeyName);
    }

}
