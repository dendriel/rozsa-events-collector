package com.rozsa.events.collector.aspects.utils;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.CollectField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldCollector {

    public static void collectField(final String flow, final Object target, final EventsCollectorManager eventsCollectorManager) throws IllegalAccessException {
        Class<?> objectClass = target.getClass();
        Field[] declaredFields = getAllFields(objectClass).toArray(new Field[0]);

        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(CollectField.class)) {
                continue;
            }

            CollectField collectField = field.getAnnotation(CollectField.class);
            Object fieldValue = field.get(target);

            if (collectField.scanFields()) {
                FieldCollector.collectField(flow, fieldValue, eventsCollectorManager);
                continue;
            }

            String targetFlow = collectField.flow().isBlank() ? flow : collectField.flow();
            String key = getKey(collectField);
            key = key == null || key.isBlank() ? field.getName() : key;
            eventsCollectorManager.collect(targetFlow, key, fieldValue);
        }
    }

    /**
     * Get declared fields from target class and all its SuperClasses (until Object.class).
     */
    private static List<Field> getAllFields(Class<?> objectClass) {
        Class<?> superclass = objectClass.getSuperclass();
        if (superclass.getName().equals(Object.class.getName())) {
            return List.of(objectClass.getDeclaredFields());
        }

        List<Field> fields = new ArrayList<>();
        fields.addAll(getAllFields(superclass));
        fields.addAll(List.of(objectClass.getDeclaredFields()));
        return fields;
    }

    private static String getKey(final CollectField collectField) {
        String key = collectField.key();

        if (key == null || key.isBlank()) {
            key = collectField.value();
        }

        return key;
    }
}
