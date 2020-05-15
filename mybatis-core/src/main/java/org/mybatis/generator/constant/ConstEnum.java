package org.mybatis.generator.constant;

public enum ConstEnum {
    COND_SUFFIX("condSuffix", "cond", "cond类后缀"),
    CONTEXT_FIELD("context_field", "context-field", "context类型属性"),
    core_project_prefix("core_project_prefix", "core_project_prefix", "context类型属性"),
    core_package_prefix("core_package_prefix", "core_package_prefix", "context类型属性"),
    api_project_prefix("api_project_prefix", "api_project_prefix", "context类型属性"),
    api_package_prefix("api_package_prefix", "api_package_prefix", "context类型属性"),
    VO_SUFFIX("voSuffix", "VO", "VO类后缀");
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

    ConstEnum(String name, String value, String desc) {
        this.name = name;
        this.value = value;
        this.desc = desc;
    }
}