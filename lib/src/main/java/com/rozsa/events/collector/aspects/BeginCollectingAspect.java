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
        final String flow = getFlow(proceedingJoinPoint);
        eventsCollectorManager.begin(flow);

        Object result;
        try {
             result = proceedingJoinPoint.proceed();
        } catch (Exception e) {
            submitOnError(proceedingJoinPoint, flow);
            throw e;
        }

        eventsCollectorManager.submit(flow);
        return result;
    }

    private void submitOnError(final JoinPoint joinPoint, final String flow) throws IOException {
        BeginCollecting beginCollecting = getAnnotationFrom(BeginCollecting.class, joinPoint);
        if (beginCollecting.submitOnError()) {
            eventsCollectorManager.submit(flow);
        }
        else {
            eventsCollectorManager.clear(flow);
        }
    }

    private String getFlow(final JoinPoint joinPoint) {
        BeginCollecting beginCollecting = getAnnotationFrom(BeginCollecting.class, joinPoint);
        return beginCollecting != null ? beginCollecting.flow() : "";
    }
}
