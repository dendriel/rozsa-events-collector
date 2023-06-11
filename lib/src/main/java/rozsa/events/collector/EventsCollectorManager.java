package rozsa.events.collector;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventsCollectorManager {

    private static final ThreadLocal<CollectionContext> collectionContext = new ThreadLocal<>();

    public void begin() {
        // TODO: get ID key from config.
        // TODO: create ID generator.
        collectionContext.set(new CollectionContext("id", "123456"));
        this.clear();
    }

    public void clear() {
        collectionContext.get().reset();
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

        public void reset() {
            collection.clear();;
        }
    }
}
