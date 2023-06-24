package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.Collect;
import com.rozsa.events.collector.annotations.CollectReturn;
import com.rozsa.events.collector.aspects.utils.FieldCollector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import static com.rozsa.events.collector.aspects.utils.AnnotationUtils.getAnnotationFrom;
import static com.rozsa.events.collector.aspects.utils.JoinPointUtils.getMethodName;

@Aspect
@Component
public class CollectReturnAspect {
    private final static Logger logger = LoggerFactory.getLogger(CollectReturnAspect.class);

    private final EventsCollectorManager eventsCollectorManager;

    public CollectReturnAspect(EventsCollectorManager eventsCollectorManager) {
        this.eventsCollectorManager = eventsCollectorManager;
    }

    @Pointcut(value="@annotation(com.rozsa.events.collector.annotations.CollectReturn)")
    public void collectReturnAnnotation() {}

    @AfterReturning(value="collectReturnAnnotation()", returning="value")
    public void collect(final JoinPoint joinPoint, final Object value) throws IllegalAccessException {
        final String flow = getFlow(joinPoint);
        CollectReturn collectReturn = getAnnotationFrom(CollectReturn.class, joinPoint);

        if (collectReturn.scanFields()) {
            FieldCollector.collectField(flow, value, eventsCollectorManager);
            return;
        }

        String key = getKey(collectReturn);

        if (key == null || key.isBlank()) {
            if (value == null) {
                logger.error("Can't collect data because there is no explicit key and return is null. {}", getMethodName(joinPoint));
                return;
            }

            key = value.getClass().getName();
        }

        eventsCollectorManager.collect(flow, key, value);
    }

    private String getKey(final CollectReturn collectReturn) {
        String key = collectReturn.key();

        if (key == null || key.isBlank()) {
            key = collectReturn.value();
        }

        return key;
    }

    private String getFlow(final JoinPoint joinPoint) {
        CollectReturn collectReturn = getAnnotationFrom(CollectReturn.class, joinPoint);
        return collectReturn != null ? collectReturn.flow() : "";
    }
}
