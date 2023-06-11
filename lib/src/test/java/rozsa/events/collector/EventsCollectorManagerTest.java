package rozsa.events.collector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EventsCollectorManagerTest {

    @Test void collectSuccess() {

        final String key = "foo";
        final String value = "bar";

        EventsCollectorManager eventsCollectorManager = new EventsCollectorManager();

        eventsCollectorManager.begin();
        eventsCollectorManager.collect(key, value);

        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        Assertions.assertEquals(1, collection.size());

        Optional<Map.Entry<String, Object>> optResult = collection.stream().filter(e -> e.getKey().equals(key)).findFirst();
        Assertions.assertTrue(optResult.isPresent());
        Assertions.assertEquals(value, optResult.get().getValue());
    }
}
