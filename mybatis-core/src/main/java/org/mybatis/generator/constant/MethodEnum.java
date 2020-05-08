package org.mybatis.generator.constant;

import org.mybatis.generator.internal.util.StringUtility;

import java.util.Objects;

public enum MethodEnum {
    RESULT_MAP("result_map", "BaseResultMap", "查询结果map"),
    SQL_CONDITION("sql_condition", "cond_selective", "查询条件"),
    BASE_COLUMN_LIST("base_column_list", "Base_Column_List", "查询列"),
    SAVE("insertSelective", "save", "新增"),
    GET("selectByPrimaryKey", "get", "根据id查询"),
    GET_ONE("getOne", "getOne", "根据po查询单个"),
    UPDATE("updateByPrimaryKeySelective", "update", "更新"),
    DELETE("deleteByPrimaryKey", "delete", "删除"),
    LIST_BY_IDS("listByIds", "listByIds", "根据ids查询列表"),
    LIST_BY_CONDITION("listByCondition", "listByCondition", "根据条件查询列表"),
    LIST("list", "list", "根据po查询列表"),
    COUNT_BY_CONDITION("countByCondition", "countByCondition", "根据条件查询列表总数"),
    COUNT("count", "count", "根据po查询列表总数"),
    MAP_BY_IDS("mapByIds", "mapByIds", "根据ids查询以主键为key的maps"),
    MAP("map", "map", "根据条件查询以主键为key的maps"),
    MAP_BY_CONDITION("mapByCondition", "mapByCondition", "根据条件查询map"),
    LIST_ID("listId", "listId", "根据条件查询ids列表"),
    SAVE_AND_GET("saveAndGet", "saveAndGet", "新增并返回"),
    BATCH_LIST("batchList", "batchList", "批量查询列表"),
    DO_BATCH("doBatchMethod", "doBatch", "处理分批查询"),
    DELETE_BY_CONDITION("deleteByCondition", "realDelete", "物理删除"),
    REAL_DELETE("realDelete", "realDelete", "物理删除"),
    SOFT_DELETE("softDelete", "softDelete", "逻辑删除");
    /**
     * 方法key
     */
    private String name;
    /**
     * 方法值
     */
    private String value;

    /**
     * 方法描述
     */
    private String desc;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    MethodEnum(String name, String value, String desc) {
        this.name = name;
        this.value = value;
        this.desc = desc;
    }


    public static String getNameByValue(String val) {
        String name = null;
        if (!StringUtility.stringHasValue(val)) {
            return null;
        }
        MethodEnum[] methodEnums = MethodEnum.values();
        for (MethodEnum methodEnum : methodEnums) {
            if (Objects.equals(methodEnum.getValue(), val)) {
                name = methodEnum.getName();
                break;
            }
        }
        return name;
    }
}