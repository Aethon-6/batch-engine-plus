package com.engine.aop.aspect;

import com.engine.aop.annotation.DS;
import com.engine.config.DynamicDataSource;
import com.engine.enums.DSTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@Order(-100)
@Slf4j
public class DSAspect {


    @Pointcut("@annotation(com.engine.aop.annotation.DS)")
    private void dsPointCut() {
    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前指定的数据源
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        DS dataSource = method.getAnnotation(DS.class);

        if (Objects.isNull(dataSource)) {
            // 使用默认数据源
            DynamicDataSource.setDataSource(DSTypeEnum.MASTER);
            log.info("未匹配到数据源，使用默认数据源");
        } else {
            // 匹配到的话，设置到动态数据源上下文中
            DynamicDataSource.setDataSource(dataSource.value());
            log.info("匹配到数据源：{}", dataSource.value().getName());
        }

        try {
            // 执行目标方法，返回值即为当前方法的返回值
            return joinPoint.proceed();
        } finally {
            // 方法执行完毕之后，销毁当前数据源信息，进行垃圾回收
            DynamicDataSource.clearDataSource();
            log.info("当前数据源已清空");
        }
    }
}
