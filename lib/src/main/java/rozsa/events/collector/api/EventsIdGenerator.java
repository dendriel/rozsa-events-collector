package rozsa.events.collector.api;

/**
 * Generates an ID to identify an event.
 */
public interface EventsIdGenerator {
    /**
     * @return an object to be used as the value of the event ID.
     */
    Object generate();
}
