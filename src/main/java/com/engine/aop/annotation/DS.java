package com.engine.aop.annotation;

import com.engine.enums.DSTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {
    DSTypeEnum value() default DSTypeEnum.MASTER;
}
