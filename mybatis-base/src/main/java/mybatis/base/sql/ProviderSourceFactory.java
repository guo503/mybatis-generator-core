package mybatis.base.sql;


import mybatis.base.exception.ProviderCreateException;
import mybatis.base.provider.BaseProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author lgt
 * @date 2019/4/29 : 7:00 PM
 */
public class ProviderSourceFactory {

    private static final Map<Class<? extends BaseProvider>, BaseProvider> PROVIDER_MAP = new HashMap<>();

    public static BaseProvider getProvider(Class<? extends BaseProvider> providerClass) {
        BaseProvider baseProvider = PROVIDER_MAP.get(providerClass);
        if (Objects.nonNull(baseProvider)) {
            return baseProvider;
        }
        try {
            baseProvider = providerClass.newInstance();
            PROVIDER_MAP.put(providerClass, baseProvider);
            return baseProvider;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ProviderCreateException("创建Provider 失败", e);
        }
    }
}
