package mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定表名和该表的命名风格
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * 指定表名
     */
    String name() default "";

    /**
     * 命名规则
     *
     * @return 命名规则
     */
    NameStyle style() default NameStyle.camelhumpAndLowercase;

}
