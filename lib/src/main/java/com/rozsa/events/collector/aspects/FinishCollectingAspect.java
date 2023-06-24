package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.FinishCollecting;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.rozsa.events.collector.aspects.utils.AnnotationUtils.getAnnotationFrom;

@Aspect
@Component
public class FinishCollectingAspect {
    private final EventsCollectorManager eventsCollectorManager;

    public FinishCollectingAspect(EventsCollectorManager eventsCollectorManager) {
        this.eventsCollectorManager = eventsCollectorManager;
    }

    @Pointcut(value="@annotation(com.rozsa.events.collector.annotations.FinishCollecting)")
    public void finishCollectingAnnotation() {}

    @After("finishCollectingAnnotation()")
    public void finishCollecting(final JoinPoint joinPoint) throws IOException {
        final String flow = getFlow(joinPoint);
        eventsCollectorManager.submit(flow);
    }

    private String getFlow(final JoinPoint joinPoint) {
        FinishCollecting finishCollecting = getAnnotationFrom(FinishCollecting.class, joinPoint);
        return finishCollecting != null ? finishCollecting.flow() : "";
    }
}
