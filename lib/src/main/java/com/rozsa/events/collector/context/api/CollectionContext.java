package com.rozsa.events.collector.context.api;

import java.util.Map;

/**
 * Holds collected events data until submission.
 */
public interface CollectionContext {
    void add(final String key, final Object value);

    Map<String, Object> getCollection();

    void clear();
}
