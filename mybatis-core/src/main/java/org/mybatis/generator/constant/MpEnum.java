package org.mybatis.generator.constant;

/**
 * @author guos
 * @date 2021/2/8 16:19
 **/
public enum MpEnum {

    TableName("com.baomidou.mybatisplus.annotation.TableName", "TableName"),
    TableId("com.baomidou.mybatisplus.annotation.TableId", "TableId"),
    IdType("com.baomidou.mybatisplus.annotation.IdType", "IdType"),
    BaseMapper("com.baomidou.mybatisplus.core.mapper.BaseMapper", "BaseMapper"),
    IService("com.baomidou.mybatisplus.extension.service.IService", "IService"),
    ServiceImpl("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl", "ServiceImpl");


    MpEnum(String path, String value) {
        this.path = path;
        this.value = value;
    }

    /**
     * 方法key
     */
    private String path;
    /**
     * 方法值
     */
    private String value;


    public java.lang.String getPath() {
        return path;
    }

    public void setPath(java.lang.String path) {
        this.path = path;
    }

    public java.lang.String getValue() {
        return value;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }
}
