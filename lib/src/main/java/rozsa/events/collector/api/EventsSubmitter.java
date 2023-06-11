package rozsa.events.collector.api;


import java.io.IOException;
import java.util.Map;

/**
 * Submits an event to the remote server.
 */
public interface EventsSubmitter {

    /**
     * Submits the given event data to the remote server.
     * @param collection event data to be submitted.
     */
    void submit(Map<String, Object> collection) throws IOException;
}
