package com.engine.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class NodeExecuteAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(com.engine.aop.annotation.NodeExecute)")
    public void nodeExecutePointCut() {

    }

    @AfterReturning(value = "nodeExecutePointCut()", returning = "rObject")
    private void afterRunningAdvance(JoinPoint point, Object rObject) {

    }
}
