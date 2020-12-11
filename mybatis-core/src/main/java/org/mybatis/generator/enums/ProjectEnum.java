package org.mybatis.generator.enums;

/**
 * author guos
 * date 2020/12/11 10:40
 **/
public enum ProjectEnum {
    BS("bs", "controller/business/service/mapper"),
    BSM("bsm", "controller/business/service/manage/mapper");

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;


    ProjectEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static boolean isBS(String code) {
        if (code == null || "".equals(code)) {
            return false;
        }
        return BS.code.equals(code);
    }
}
