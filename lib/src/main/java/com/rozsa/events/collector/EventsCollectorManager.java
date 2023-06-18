package com.rozsa.events.collector;

import com.rozsa.events.collector.api.EventsIdGenerator;
import com.rozsa.events.collector.api.EventsSubmitter;
import com.rozsa.events.collector.context.CollectionContextHandler;
import com.rozsa.events.collector.context.api.CollectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class EventsCollectorManager {
    public static final Logger log = LoggerFactory.getLogger(EventsCollectorManager.class);
    private static final ThreadLocal<CollectionContextHandler> collections = ThreadLocal.withInitial(CollectionContextHandler::new);
    private static final String defaultFlow = "default";

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
        begin(defaultFlow);
    }

    public void begin(final String flow) {
        log.debug("[flow:{}] Begin collecting event data.", flow);

        getCollectionContextHandler().initialize(flow);

        Object id = eventsIdGenerator.generate();
        collect(flow, idFieldKey, id);

        log.debug("[flow:{}] Collection context has initialized. Current event ID is '{}={}'", flow, idFieldKey, id);
    }

    /**
     * Clears all saved event data, including generated ID. Should be used carefully.
     */
    public void clear() {
        clear(defaultFlow);
    }

    public void clear(final String flow) {
        log.debug("[flow:{}] Clearing the event data.", flow);

        getFlow(flow).clear();
    }

    public void clearAll() {
        getCollectionContextHandler().clearAll();
    }

    public void collect(final String key, final Object value) {
        collect(defaultFlow, key, value);
    }

    public void collect(final String flow, final String key, final Object value) {
        log.debug("[flow:{}] Collection event data '{}={}'", flow, key, value);

       getFlow(flow).add(key, value);
    }

    public void submit() throws IOException {
        submit(defaultFlow);
    }

    public void submit(final String flow) throws IOException {
        log.debug("[flow:{}] Submitting the event to remote server.", flow);

        eventsSubmitter.submit(getFlow(flow).getCollection());

        clear(flow);
    }

    /**
     * @return Creates and returns a shallow copy of the collected data so far.
     */
    public Set<Map.Entry<String, Object>> getCollection() {
        return getCollection(defaultFlow);
    }

    public Set<Map.Entry<String, Object>> getCollection(final String flow) {
        return getFlow(flow).getCollection().entrySet();
    }

    private CollectionContext getFlow(final String flow) {
        return collections.get().get(flow);
    }

    private CollectionContextHandler getCollectionContextHandler() {
        return collections.get();
    }
}

