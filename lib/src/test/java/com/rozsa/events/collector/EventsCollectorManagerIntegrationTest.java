package com.rozsa.events.collector;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static testutils.AsyncTestUtils.verifyAsync;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {EventsCollectorAutoConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWireMock(port = 0)
public class EventsCollectorManagerIntegrationTest {

    @Autowired
    private EventsCollectorManager eventsCollectorManager;

    private final String eventIdKey = "event_id";

    @BeforeEach
    void setup() {
        // Collection Context is static, so we have to clean it manually.
        eventsCollectorManager.clear();
    }

    @Test
    void givenBeginWasNotCalledBefore_whenBeginIsCalled_thenEventIdShouldBeGenerated() {
        eventsCollectorManager.begin();
        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        assertEquals(1, collection.size());

        Optional<Map.Entry<String, Object>> optResult = collection.stream().filter(e -> e.getKey().equals(eventIdKey)).findFirst();
        assertTrue(optResult.isPresent());

        Object value = optResult.get().getValue();
        assertNotNull(value);
    }

    @Test
    void givenNoCollectionWasMade_whenCollectIsCalled_thenCollectedValueShouldBeInCollection() {
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

    @Test
    void givenBeginWasCalledBefore_whenBeginIsCalledAgain_thenPreviouslyCollectedDataShouldBeCleared() {

        // after this, collection has two elements ('id' and 'foo').
        eventsCollectorManager.begin();
        eventsCollectorManager.collect("foo", "bar");

        // then calling begin() will clear previous data and insert a newly generated ID.
        eventsCollectorManager.begin();

        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        assertEquals(1, collection.size());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"", " "})
    void givenEmptyStringsAsKeys_whenCollectIfCalled_thenIllegalArgumentExceptionShouldBeThrown(String key) {

        eventsCollectorManager.begin();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                eventsCollectorManager.collect(key, "foo")
        );

        assertEquals("Can't use an empty key to collect data.", thrown.getMessage());
    }

    @Test
    void givenANullKey_whenCollectIfCalled_thenIllegalArgumentExceptionShouldBeThrown() {
        eventsCollectorManager.begin();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                eventsCollectorManager.collect(null, "foo")
        );

        assertEquals("Can't use an empty key to collect data.", thrown.getMessage());
    }

    @Test
    void givenNothingCollected_whenClearIsCalled_thenCollectionShouldBeEmptied() {
        eventsCollectorManager.begin();
        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        assertEquals(1, collection.size());

        eventsCollectorManager.clear();
        assertEquals(0, collection.size());
    }

    @Test
    void givenDataCollected_whenClearIsCalled_thenCollectionShouldBeEmptied() {
        eventsCollectorManager.begin();

        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        eventsCollectorManager.collect("foo", "bar");


        eventsCollectorManager.clear();
        assertEquals(0, collection.size());
    }

    @Test
    void givenClearIsCalledBefore_whenClearIsCalledAgain_thenNothingShouldChange() {
        eventsCollectorManager.begin();
        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();

        eventsCollectorManager.clear();
        assertEquals(0, collection.size());

        eventsCollectorManager.clear();
        assertEquals(0, collection.size());
    }

    @Test
    void givenCollectionNotInitialized_whenSubmitIsCalled_thenRemoteServerShouldNotBeCalled() throws IOException {
        eventsCollectorManager.submit();

        verify(0, postRequestedFor(urlMatching("/collect")));
    }

    @Test
    void givenDataCollected_whenSubmitIsCalled_thenRemoteServerShouldBeCalledAndCollectionIsCleared() throws IOException, InterruptedException {

        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                )
        );

        final String key = "foo";
        final String value = "bar";

        eventsCollectorManager.begin();
        eventsCollectorManager.collect(key, value);
        eventsCollectorManager.submit();

        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
                .withRequestBody(matchingJsonPath("$[0].foo", containing(value)))
                .withRequestBody(matchingJsonPath("$[0].event_id", matching("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))),
                1000
        );


        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection();
        assertEquals(0, collection.size());
    }
}
