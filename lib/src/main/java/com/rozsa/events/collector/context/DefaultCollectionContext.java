package com.rozsa.events.collector.context;

import com.rozsa.events.collector.context.api.CollectionContext;

import java.util.HashMap;
import java.util.Map;


public class DefaultCollectionContext implements CollectionContext {
    private final Map<String, Object> collection;


    public DefaultCollectionContext() {
        this.collection = new HashMap<>();
    }

    @Override
    public void add(final String key, final Object value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Can't use an empty key to collect data.");
        }

        collection.put(key, value);
    }

    @Override
    public Map<String, Object> getCollection() {
        return collection;
    }

    @Override
    public void clear() {
        collection.clear();
    }
}
