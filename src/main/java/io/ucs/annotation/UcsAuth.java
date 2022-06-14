package io.ucs.annotation;

import io.ucs.handler.Handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Macrow
 * @date 2022/06/11
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface UcsAuth {
    Class<?> afterHandler() default Handler.class;
}
