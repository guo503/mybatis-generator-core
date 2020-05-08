package mybatis.base.sql;


import mybatis.base.meta.EntityInfo;
import mybatis.base.provider.BaseProvider;
import mybatis.base.provider.ParamProviderContext;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author lgt
 * @date 2019/4/29 : 6:00 PM
 */
public class ExtProviderSqlSource implements SqlSource {


    private final Configuration configuration;
    private final SqlSourceBuilder sqlSourceParser;
    private Method mapperMethod;
    private Class<?> mapperType;
    private BaseProvider baseProvider;
    private EntityInfo entityInfo;
    private String[] parametersAlias;


    public ExtProviderSqlSource(Configuration configuration, Class<BaseProvider> baseProvider, Class<?> mapperType, Method mapperMethod, EntityInfo entityInfo) {
        this.configuration = configuration;
        this.mapperMethod = mapperMethod;
        this.mapperType = mapperType;
        this.sqlSourceParser = new SqlSourceBuilder(configuration);
        this.baseProvider = ProviderSourceFactory.getProvider(baseProvider);
        this.entityInfo = entityInfo;
        this.parametersAlias = parseMethodParameters(mapperMethod);
    }

    private String[] parseMethodParameters(Method mapperMethod) {
        Parameter[] parameters = mapperMethod.getParameters();
        String[] parametersAlias = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Param param = parameters[i].getAnnotation(Param.class);
            if (Objects.nonNull(param)) {
                parametersAlias[i] = param.value();
            } else{
                parametersAlias[i] = String.valueOf(i);
            }
        }
        return parametersAlias;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        SqlSource sqlSource = createSqlSource(parameterObject);
        return sqlSource.getBoundSql(parameterObject);
    }

    private SqlSource createSqlSource(Object parameterObject) {
        ParamProviderContext paramProviderContext = new ParamProviderContext();
        paramProviderContext.setMapperMethod(this.mapperMethod);
        paramProviderContext.setMapperType(this.mapperType);
        paramProviderContext.setEntityInfo(this.entityInfo);
        paramProviderContext.setParameters(parameterObject);
        paramProviderContext.setParametersAlias(this.parametersAlias);
        String sql = baseProvider.produce(paramProviderContext);

        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        return sqlSourceParser.parse(replacePlaceholder(sql), parameterType, new HashMap<>());

    }

    private String replacePlaceholder(String sql) {
        return PropertyParser.parse(sql, configuration.getVariables());
    }

}
