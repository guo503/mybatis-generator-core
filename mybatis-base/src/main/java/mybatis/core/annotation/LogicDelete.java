package mybatis.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 逻辑删除
 *
 * @author lgt
 * @date 2019/4/28 : 9:39 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicDelete {

    int isDelete() default 1;

    int isNotDelete() default 0;

}
