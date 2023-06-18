package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.CollectParameter;
import com.rozsa.events.collector.api.ObjectCollector;
import com.rozsa.events.collector.cached.ObjectCollectorManager;
import com.rozsa.events.collector.aspects.utils.FieldCollector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class CollectAspect {
    private final EventsCollectorManager eventsCollectorManager;

    private final ObjectCollectorManager objectCollectorManager;

    public CollectAspect(EventsCollectorManager eventsCollectorManager, ObjectCollectorManager objectCollectorManager) {
        this.eventsCollectorManager = eventsCollectorManager;
        this.objectCollectorManager = objectCollectorManager;
    }

    @Pointcut(value = "@annotation(com.rozsa.events.collector.annotations.Collect)")
    public void collectAnnotation() {
    }

    @Before("collectAnnotation()")
    public void collect(final JoinPoint joinPoint) throws IllegalAccessException {
        Object[] args = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        /* TODO
         * cache key.
         * signature.getName()
         * signature.getDeclaringTypeName()
         */
        Method method = signature.getMethod();

        // TODO: cache method parameters.
        Parameter[] params = method.getParameters(); // um pouco lento

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];

            if (!param.isAnnotationPresent(CollectParameter.class)) {
                continue;
            }

            final Object value = args[i];
            CollectParameter collectParameter = param.getAnnotation(CollectParameter.class);

            if (!collectParameter.collector().isBlank()) {
                String collectorName = collectParameter.collector();
                ObjectCollector collector = objectCollectorManager.getBean(collectorName);
                collector.collect(value, eventsCollectorManager);
                return;
            }

            if (collectParameter.scanFields()) {
                FieldCollector.collectField(value, eventsCollectorManager);
                continue;
            }

            String key = getKey(collectParameter);
            key = key == null || key.isBlank() ? param.getName() : key;
            eventsCollectorManager.collect(key, value);
        }
    }

    private String getKey(final CollectParameter collectParameter) {
        String key = collectParameter.key();

        if (key == null || key.isBlank()) {
            key = collectParameter.value();
        }

        return key;
    }
}
