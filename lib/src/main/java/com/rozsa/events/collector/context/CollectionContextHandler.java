package com.rozsa.events.collector.context;

import com.rozsa.events.collector.context.api.CollectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CollectionContextHandler {
    private static final Logger logger = LoggerFactory.getLogger(NullCollectionContext.class);

    private final Map<String, CollectionContext> contexts;

    private final CollectionContext nullCollectionContext;

    public CollectionContextHandler() {
        this.contexts = new HashMap<>();

        nullCollectionContext = new NullCollectionContext();
    }

    public void initialize(final String flow) {
        CollectionContext collectionContext = new DefaultCollectionContext();
        contexts.put(flow, collectionContext);
    }

    public CollectionContext get(final String flow) {
        if (!contexts.containsKey(flow)) {
            logger.warn("Cannot access flow '{}' because it was not initialized!", flow);
            return nullCollectionContext;
        }

        return contexts.get(flow);
    }

    public void clearAll() {
        contexts.clear();
    }

    public int count() {
        return contexts.size();
    }
}
