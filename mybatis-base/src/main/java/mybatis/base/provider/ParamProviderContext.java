package mybatis.base.provider;


import mybatis.base.meta.EntityInfo;

import java.lang.reflect.Method;

/**
 * @author lgt
 * @date 2019/4/29 : 7:28 PM
 */
public class ParamProviderContext {

    private Method mapperMethod;

    private Class<?> mapperType;

    private Object parameters;

    private EntityInfo entityInfo;

    private String[] parametersAlias;

    public String[] getParametersAlias() {
        return parametersAlias;
    }

    public void setParametersAlias(String[] parametersAlias) {
        this.parametersAlias = parametersAlias;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public Method getMapperMethod() {
        return mapperMethod;
    }

    public void setMapperMethod(Method mapperMethod) {
        this.mapperMethod = mapperMethod;
    }

    public Class<?> getMapperType() {
        return mapperType;
    }

    public void setMapperType(Class<?> mapperType) {
        this.mapperType = mapperType;
    }

    public Object getParameters() {
        return parameters;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }
}
