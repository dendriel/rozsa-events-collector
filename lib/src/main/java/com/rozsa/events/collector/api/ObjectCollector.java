package com.rozsa.events.collector.api;

import com.rozsa.events.collector.EventsCollectorManager;

@FunctionalInterface
public interface ObjectCollector {

    /**
     * Defines custom logic for collecting data from an object
     * @param flow collection flow
     * @param source original object market for collection.
     * @param eventsCollectorManager collection manager to collect data.
     */
    void collect(String flow, Object source, EventsCollectorManager eventsCollectorManager);
}
