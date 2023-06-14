package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.BeginCollecting;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;

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

    private void submitOnError(final ProceedingJoinPoint proceedingJoinPoint) throws IOException {
        if (isSubmitOnError(proceedingJoinPoint)) {
            eventsCollectorManager.submit();
        }
        else {
            eventsCollectorManager.clear();
        }
    }

    // TODO: cache reflection stuff
    private Boolean isSubmitOnError(final ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        BeginCollecting beginCollecting = method.getAnnotation(BeginCollecting.class);
        return beginCollecting.submitOnError();
    }
}
