package com.engine.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GlobalExceptionAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(* com.engine..*.*(..))")
    public void pointcut() {

    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("系统错误：{}", e.getMessage());
    }
}
