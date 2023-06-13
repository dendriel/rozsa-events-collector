package com.rozsa.events.collector.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CollectAspect {
    @Pointcut(value="@annotation(com.rozsa.events.collector.annotations.Collect)")
    public void collectAnnotation() {}

    @Before("collectAnnotation()")
    public void collect(JoinPoint joinPoint) {

    }
}
