package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Aspect
@Component
public class FinishCollectingAspect {
    private final EventsCollectorManager eventsCollectorManager;

    public FinishCollectingAspect(EventsCollectorManager eventsCollectorManager) {
        this.eventsCollectorManager = eventsCollectorManager;
    }

    @Pointcut(value="@annotation(com.rozsa.events.collector.annotations.FinishCollecting)")
    public void finishCollectingAnnotation() {}

    @Before("finishCollectingAnnotation()")
    public void finishCollecting() throws IOException {
        eventsCollectorManager.submit();
    }
}
