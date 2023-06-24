package com.rozsa.events.collector.cached;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.api.ObjectCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Proxy class to cache applicationContext results.
 */
public class ObjectCollectorManager {
    Logger logger = LoggerFactory.getLogger(ObjectCollectorManager.class);

    private final ApplicationContext applicationContext;

    private final Map<String, ObjectCollector> collectors;

    public ObjectCollectorManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        collectors = new HashMap<>();
    }

    public ObjectCollector getBean(final String key) {
        if (collectors.containsKey(key)) {
            return collectors.get(key);
        }

        ObjectCollector collector = this::emptyCollector;
        try {
            collector = applicationContext.getBean(key, ObjectCollector.class);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("Bean '{}' was not found!", key, e);
        } catch (BeanNotOfRequiredTypeException e) {
            logger.error("Bean '{}' is not an ObjectCollector!", key, e);
        } catch (BeansException e ) {
            logger.error("Failed to create bean '{}'!", key, e);
        }

        collectors.put(key, collector);

        return collector;
    }

    private void emptyCollector(String flow, Object source, EventsCollectorManager eventsCollectorManager) {}
}
