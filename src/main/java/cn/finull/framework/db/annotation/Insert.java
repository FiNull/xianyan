package cn.finull.framework.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Insert {
    /**
     * 插入语句sql
     */
    String value() default "";

    /**
     * 是否返回生成的主键，默认不返回
     */
    boolean generatedKey() default false;
}
