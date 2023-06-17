package com.rozsa.events.collector;

import com.rozsa.events.collector.api.EventsIdGenerator;
import com.rozsa.events.collector.api.EventsSubmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventsCollectorManager {
    public static final Logger log = LoggerFactory.getLogger(EventsCollectorManager.class);

    private static final ThreadLocal<CollectionContext> collectionContext = ThreadLocal.withInitial(CollectionContext::new);

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
        log.debug("Begin collecting event data.");

        clear();
        Object id = eventsIdGenerator.generate();
        collect(idFieldKey, id);

        log.debug("Collection context has initialized. Current event ID is '{}={}'", idFieldKey, id);
    }

    /**
     * Clears all saved event data, including generated ID. Should be used carefully.
     */
    public void clear() {
        log.debug("Clearing the event data.");

        collectionContext.get().clear();
    }

    public void collect(final String key, final Object value) {
        log.debug("Collection event data '{}={}'", key, value);

        collectionContext.get().add(key, value);
    }

    public void submit() throws IOException {
        log.debug("Submitting the event to remote server.");

        eventsSubmitter.submit(collectionContext.get().getCollection());

        clear();
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

        public CollectionContext() {
            this.collection = new HashMap<>();
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
            collection.clear();
        }
    }
}

