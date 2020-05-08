package mybatis.base.build;


import mybatis.core.mapper.BaseMapper;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展mapper注册器
 *
 * @author lgt
 * @date 2019/4/30 : 9:38 AM
 */
public class ExtMapperRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ExtMapperRegistry.class);

    private Configuration configuration;

    public ExtMapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 注册所有的
     *
     * @param type 需要注册的Mapper
     * @param <T>  mapper的类型
     */
    public <T> void addMapper(Class<T> type) {
        if (!configuration.hasMapper(type)) {
            configuration.addMapper(type);
        }
        this.addMapperOnlyExt(type);
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.configuration.setUseGeneratedKeys(useGeneratedKeys);
    }

    /**
     * 只注册扩展的
     *
     * @param type 需要注册的Mapper
     * @param <T>  mapper的类型
     */
    public <T> void addMapperOnlyExt(Class<T> type) {
        if (!BaseMapper.class.isAssignableFrom(type)) {
            logger.info("type :{} 不属于 BaseMapper ，不需要进行注册扩展的方法", type);
            return;
        }
        logger.info("type :{} 属于 BaseMapper ，进行加载", type);
        ExtMapperAnnotationBuilder extMapperAnnotationBuilder = new ExtMapperAnnotationBuilder(configuration, type);
        extMapperAnnotationBuilder.addMapperOnlyExt(type);
    }

}
