package mybatis.base.provider.anno;


import mybatis.base.provider.BaseProvider;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SelectSqlProvider {

    Class<? extends BaseProvider> provider();

}
