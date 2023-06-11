package rozsa.events.collector;

import rozsa.events.collector.api.EventsIdGenerator;

import static java.util.UUID.randomUUID;

public class UUIDEventsIdGenerator implements EventsIdGenerator {

    public Object generate() {
        return randomUUID().toString();
    }
}
