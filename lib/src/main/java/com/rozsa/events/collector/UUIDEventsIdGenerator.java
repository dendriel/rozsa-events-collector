package com.rozsa.events.collector;

import com.rozsa.events.collector.api.EventsIdGenerator;

import static java.util.UUID.randomUUID;

public class UUIDEventsIdGenerator implements EventsIdGenerator {

    public Object generate() {
        return randomUUID().toString();
    }
}
