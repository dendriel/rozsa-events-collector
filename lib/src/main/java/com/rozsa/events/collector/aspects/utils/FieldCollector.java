package com.rozsa.events.collector.aspects.utils;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.CollectField;
import com.rozsa.events.collector.annotations.CollectReturn;

import java.lang.reflect.Field;

public class FieldCollector {

    public static void collectField(final Object target, final EventsCollectorManager eventsCollectorManager) throws IllegalAccessException {
        Class<?> objectClass = target.getClass();
        Field[] declaredFields = objectClass.getDeclaredFields();

        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(CollectField.class)) {
                continue;
            }

            CollectField collectField = field.getAnnotation(CollectField.class);
            Object fieldValue = field.get(target);

            if (collectField.scanFields()) {
                FieldCollector.collectField(fieldValue, eventsCollectorManager);
                continue;
            }

            String key = getKey(collectField);
            key = key == null || key.isBlank() ? field.getName() : key;
            eventsCollectorManager.collect(key, fieldValue);
        }
    }

    private static String getKey(final CollectField collectField) {
        String key = collectField.key();

        if (key == null || key.isBlank()) {
            key = collectField.value();
        }

        return key;
    }
}
