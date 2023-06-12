package com.rozsa.events.collector;

import com.rozsa.events.collector.api.EventsIdGenerator;
import com.rozsa.events.collector.api.EventsSubmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventsCollectorManager {
    private static final ThreadLocal<CollectionContext> collectionContext = new ThreadLocal<>();

    private final String idFieldKey;
    private final EventsIdGenerator eventsIdGenerator;
    private final EventsSubmitter eventsSubmitter;

    public EventsCollectorManager(
            final String idFieldKey,
            final EventsIdGenerator eventsIdGenerator,
            final EventsSubmitter eventsSubmitter
        ) {
        this.idFieldKey = idFieldKey;
        this.eventsIdGenerator = eventsIdGenerator;
        this.eventsSubmitter = eventsSubmitter;
    }

    public void begin() {
        Object id = eventsIdGenerator.generate();
        collectionContext.set(new CollectionContext(idFieldKey, id));
    }

    public void clear() {
        CollectionContext context = collectionContext.get();
        if (context == null) {
            return;
        }

        context.clear();
    }

    public void collect(final String key, final Object value) {
        CollectionContext context = collectionContext.get();
        if (context == null) {
            return;
        }
        context.add(key, value);
    }

    public void submit() throws IOException {
        CollectionContext context = collectionContext.get();
        if (context == null) {
            return;
        }
        eventsSubmitter.submit(context.getCollection());
    }

    /**
     * @return Creates and returns a shallow copy of the collected data so far.
     */
    public Set<Map.Entry<String, Object>> getCollection() {
        return collectionContext.get().getCollection().entrySet();
    }

    /**
     * Collected data storage context.
     */
    public static class CollectionContext {
        private final Map<String, Object> collection;

        public CollectionContext(final String idKey, final Object id) {
            this.collection = new HashMap<>();
            add(idKey, id);
        }

        public void add(final String key, final Object value) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("Can't use an empty key to collect data.");
            }

            collection.put(key, value);
        }

        public Map<String, Object> getCollection() {
            return collection;
        }

        public void clear() {
            collection.clear();;
        }
    }
}
