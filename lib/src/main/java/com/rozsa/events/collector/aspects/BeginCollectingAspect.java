package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BeginCollectingAspect {
    private final EventsCollectorManager eventsCollectorManager;

    public BeginCollectingAspect(EventsCollectorManager eventsCollectorManager) {
        this.eventsCollectorManager = eventsCollectorManager;
    }

    @Pointcut(value="@annotation(com.rozsa.events.collector.annotations.BeginCollecting)")
    public void beginCollectingAnnotation() {}

    @Around("beginCollectingAnnotation()")
    public Object beginCollecting(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        eventsCollectorManager.begin();

        try {
            return proceedingJoinPoint.proceed();
        } finally {
            eventsCollectorManager.submit();
        }
    }
}
