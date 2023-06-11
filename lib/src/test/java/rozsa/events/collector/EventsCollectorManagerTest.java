package rozsa.events.collector;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = { EventsCollectorAutoConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWireMock(port = 0)
public class EventsCollectorManagerTest {

    @Autowired
    EventsCollectorManager eventsCollectorManager;

    private final String eventIdKey = "event_id";

    @Test
    void beginSuccess() {
        eventsCollectorManager.begin();
        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        assertEquals(1, collection.size());

        Optional<Map.Entry<String, Object>> optResult = collection.stream().filter(e -> e.getKey().equals(eventIdKey)).findFirst();
        assertTrue(optResult.isPresent());

        Object value = optResult.get().getValue();
        assertNotNull(value);
    }

    @Test
    void collectSuccess() {
        final String key = "foo";
        final String value = "bar";

        eventsCollectorManager.begin();
        eventsCollectorManager.collect(key, value);

        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        assertEquals(2, collection.size());

        Optional<Map.Entry<String, Object>> optResult = collection.stream().filter(e -> e.getKey().equals(key)).findFirst();
        assertTrue(optResult.isPresent());
        assertEquals(value, optResult.get().getValue());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"", " "})
    void collectEmptyKey(String key) {

        eventsCollectorManager.begin();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            eventsCollectorManager.collect(key, "foo")
        );

        assertEquals("Can't use an empty key to collect data.", thrown.getMessage());
    }

    @Test
    void collectNullKey() {
        eventsCollectorManager.begin();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                eventsCollectorManager.collect(null, "foo")
        );

        assertEquals("Can't use an empty key to collect data.", thrown.getMessage());
    }

    @Test
    void clearSuccess() {
        eventsCollectorManager.begin();
        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        assertEquals(1, collection.size());

        eventsCollectorManager.clear();
        assertEquals(0, collection.size());

        eventsCollectorManager.clear();
        assertEquals(0, collection.size());
    }

    @Test
    void submit() throws IOException, InterruptedException {
        final String key = "foo";
        final String value = "bar";

        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                )
        );

        eventsCollectorManager.begin();
        eventsCollectorManager.collect(key, value);
        eventsCollectorManager.submit();

        Thread.sleep(100);

        verify(1, postRequestedFor(urlMatching("/collect"))
                        .withRequestBody(matchingJsonPath("$[0].foo", containing("bar")))
                        .withRequestBody(matchingJsonPath("$[0].event_id", matching("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")))
        );
    }
}
