package com.rozsa.events.collector.api;

import com.rozsa.events.collector.EventsCollectorManager;

@FunctionalInterface
public interface ObjectCollector {

    /**
     * Defines custom logic for collecting data from an object
     * @param source Original object market for collection.
     * @param eventsCollectorManager collection manager to collect data.
     */
    void collect(Object source, EventsCollectorManager eventsCollectorManager);
}
