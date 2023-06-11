package rozsa.events.collector;

import rozsa.events.collector.api.EventsIdGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventsCollectorManager {
    private static final ThreadLocal<CollectionContext> collectionContext = new ThreadLocal<>();

    private final String idFieldKey;
    private final EventsIdGenerator eventsIdGenerator;

    public EventsCollectorManager(final String idFieldKey, final EventsIdGenerator eventsIdGenerator) {
        this.idFieldKey = idFieldKey;
        this.eventsIdGenerator = eventsIdGenerator;
    }

    public void begin() {
        Object id = eventsIdGenerator.generate();
        collectionContext.set(new CollectionContext(idFieldKey, id));
    }

    public void clear() {
        collectionContext.get().clear();
    }

    public void collect(final String key, final Object value) {
        collectionContext.get().add(key, value);
    }

    public void submit() {

    }

    /**
     * @return Creates and returns a shallow copy of the collected data so far.
     */
    public Set<Map.Entry<String, Object>> getCollection() {
        return collectionContext.get().getCollection();
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

        public Set<Map.Entry<String, Object>> getCollection() {
            return collection.entrySet();
        }

        public void clear() {
            collection.clear();;
        }
    }
}
