package mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键注解
 * 主键默认不能新增，不能修改
 *
 * @author lgt
 * @date 2019/5/24 : 5:50 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

    /**
     * 主键字段名字
     */
    String name() default "";

    /**
     * 主键生成策略 目前只支持 自增主键
     *
     * @return 主键生成策略
     */
    GenerationType strategy() default GenerationType.IDENTITY;

}
