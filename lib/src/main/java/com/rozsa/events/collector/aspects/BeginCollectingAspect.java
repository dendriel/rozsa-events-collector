package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.BeginCollecting;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.rozsa.events.collector.aspects.utils.AnnotationUtils.getAnnotationFrom;

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

        Object result;
        try {
             result = proceedingJoinPoint.proceed();
        } catch (Exception e) {
            submitOnError(proceedingJoinPoint);
            throw e;
        }

        eventsCollectorManager.submit();
        return result;
    }

    private void submitOnError(final JoinPoint joinPoint) throws IOException {
        BeginCollecting beginCollecting = getAnnotationFrom(BeginCollecting.class, joinPoint);
        if (beginCollecting.submitOnError()) {
            eventsCollectorManager.submit();
        }
        else {
            eventsCollectorManager.clear();
        }
    }
}
