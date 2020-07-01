package mybatis.core.utils;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ReflectionKit {
    private static final Map<Class<?>, List<Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new IdentityHashMap(8);

    public ReflectionKit() {
    }

    public static Object getMethodValue(Class<?> cls, Object entity, String str) {
        Map fieldMaps = getFieldMap(cls);
        if(Objects.isNull(fieldMaps) || fieldMaps.isEmpty()){
            throw new RuntimeException(String.format("Error: NoSuchField in %s for %s.  Cause:",new Object[]{cls.getSimpleName(), str}));
        }
        try {
            Method method = cls.getMethod(guessGetterName((Field)fieldMaps.get(str), str));
            return method.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("getMethodValue fail");
        }
    }

    private static String guessGetterName(Field field, final String str) {
        return StrUtils.guessGetterName(str, field.getType());
    }

    public static Object getMethodValue(Object entity, String str) {
        return null == entity ? null : getMethodValue(entity.getClass(), entity, str);
    }


    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        List<Field> fieldList = getFieldList(clazz);
        return !CollectionUtils.isEmpty(fieldList) ? (Map)fieldList.stream().collect(Collectors.toMap(Field::getName, (field) -> {
            return field;
        })) : Collections.emptyMap();
    }

    public static List<Field> getFieldList(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        } else {
            List<Field> fields = (List)CLASS_FIELD_CACHE.get(clazz);
            if (CollectionUtils.isEmpty(fields)) {
                synchronized(CLASS_FIELD_CACHE) {
                    fields = doGetFieldList(clazz);
                    CLASS_FIELD_CACHE.put(clazz, fields);
                }
            }

            return fields;
        }
    }

    public static List<Field> doGetFieldList(Class<?> clazz) {
        if (clazz.getSuperclass() != null) {
            Map<String, Field> fieldMap = excludeOverrideSuperField(clazz.getDeclaredFields(), getFieldList(clazz.getSuperclass()));
            List<Field> fieldList = new ArrayList();
            fieldMap.forEach((k, v) -> {
                if (!Modifier.isStatic(v.getModifiers()) && !Modifier.isTransient(v.getModifiers())) {
                    fieldList.add(v);
                }

            });
            return fieldList;
        } else {
            return Collections.emptyList();
        }
    }

    public static Map<String, Field> excludeOverrideSuperField(Field[] fields, List<Field> superFieldList) {
        Map<String, Field> fieldMap = (Map)Stream.of(fields).collect(Collectors.toMap(Field::getName, Function.identity(), (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new));
        superFieldList.stream().filter((field) -> {
            return !fieldMap.containsKey(field.getName());
        }).forEach((f) -> {
            Field var10000 = (Field)fieldMap.put(f.getName(), f);
        });
        return fieldMap;
    }

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, Boolean.TYPE);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, Byte.TYPE);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, Character.TYPE);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, Double.TYPE);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, Float.TYPE);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, Integer.TYPE);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, Long.TYPE);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, Short.TYPE);
    }
}
