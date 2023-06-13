package com.rozsa.events.collector.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FinishCollectingAspect {
    @Pointcut(value="@annotation(com.rozsa.events.collector.annotations.FinishCollecting)")
    public void finishCollectingAnnotation() {}

    @Before("finishCollectingAnnotation()")
    public void finishCollecting(JoinPoint joinPoint) {

    }
}
