package com.rozsa.events.collector.context;

import com.rozsa.events.collector.context.api.CollectionContext;

import java.util.Map;

public class NullCollectionContext implements CollectionContext {
    @Override
    public void add(String key, Object value) {}

    @Override
    public Map<String, Object> getCollection() {
        return Map.of();
    }

    @Override
    public void clear() {}
}
