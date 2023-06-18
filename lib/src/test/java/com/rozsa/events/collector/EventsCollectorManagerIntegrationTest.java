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
        eventsCollectorManager.clearAll();
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

    @Test
    void givenMultipleFlowsCollected_whenSubmitIsCalledForAFlow_thenExpectedDataShouldBePublished() throws IOException, InterruptedException {
        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                )
        );

        eventsCollectorManager.begin();
        eventsCollectorManager.collect("key01", 123);
        eventsCollectorManager.collect("key02", false);


        final String flow02Name = "flow02";
        final String key = "foo";
        final String value = "bar";

        eventsCollectorManager.begin(flow02Name);
        eventsCollectorManager.collect(flow02Name, key, value);
        eventsCollectorManager.submit(flow02Name);

        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
                        .withRequestBody(matchingJsonPath("$[0].foo", containing(value)))
                        .withRequestBody(matchingJsonPath("$[0].event_id", matching("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))),
                1000
        );

        Set<Map.Entry<String, Object>> collection = eventsCollectorManager.getCollection(flow02Name);
        assertEquals(0, collection.size());
    }

    @Test
    void givenMultipleFlowsCollected_whenSubmitIsCalledForDefaultFlow_thenExpectedDataShouldBePublished() throws IOException, InterruptedException {
        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                )
        );

        final String flow02Name = "flow02";
        final String key = "foo";
        final String value = "bar";

        eventsCollectorManager.begin(flow02Name);
        eventsCollectorManager.collect(flow02Name, "key01", 123);
        eventsCollectorManager.collect(flow02Name, "key02", false);

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

    @Test
    void givenMultipleFlowsInitialized_whenCollectIsCalledForAFlow_thenCollectedDataShouldBeSaveInTargetFlow() {
        final String flow02Name = "flow02";
        final String flow03Name = "flow03";

        // default flow
        eventsCollectorManager.begin();
        eventsCollectorManager.collect("def_key_01", "bar");
        eventsCollectorManager.collect("def_key_02", 1234);

        eventsCollectorManager.begin(flow02Name);
        eventsCollectorManager.collect(flow02Name, "fl02_key_01", true);
        eventsCollectorManager.collect(flow02Name, "fl02_key_02", 33.44f);

        eventsCollectorManager.begin(flow03Name);
        eventsCollectorManager.collect(flow03Name, "fl03_key_01", 90000L);

        // randomly collect data.
        eventsCollectorManager.collect(flow02Name, "fl02_key_03", "xxxxx");
        eventsCollectorManager.collect(flow03Name, "fl03_key_02", "key 2");
        eventsCollectorManager.collect(flow02Name, "fl02_key_04", 90.001D);
        eventsCollectorManager.collect("def_key_04", 456);

        // Each flow is initialized with an ID entry (thus, expected value is always plus 1).

        assertEquals(4, eventsCollectorManager.getCollection().size());
        assertEquals(5, eventsCollectorManager.getCollection(flow02Name).size());
        assertEquals(3, eventsCollectorManager.getCollection(flow03Name).size());
    }

    @Test
    void givenMultipleFlowsCollected_whenSubmitIsCalledForAFlow_thenOtherFlowsShouldNotBeAffected() throws IOException {
        final String flow02Name = "flow02";
        final String flow03Name = "flow03";

        // default flow
        eventsCollectorManager.begin();
        eventsCollectorManager.collect("def_key_01", "bar");

        eventsCollectorManager.begin(flow02Name);
        eventsCollectorManager.collect(flow02Name, "fl02_key_01", true);

        eventsCollectorManager.begin(flow03Name);
        eventsCollectorManager.collect(flow03Name, "fl03_key_01", 90000L);

        eventsCollectorManager.submit();

        assertEquals(0, eventsCollectorManager.getCollection().size());
        assertEquals(2, eventsCollectorManager.getCollection(flow02Name).size());
        assertEquals(2, eventsCollectorManager.getCollection(flow03Name).size());

        eventsCollectorManager.submit(flow03Name);
        assertEquals(0, eventsCollectorManager.getCollection().size());
        assertEquals(2, eventsCollectorManager.getCollection(flow02Name).size());
        assertEquals(0, eventsCollectorManager.getCollection(flow03Name).size());

        eventsCollectorManager.submit(flow02Name);
        assertEquals(0, eventsCollectorManager.getCollection().size());
        assertEquals(0, eventsCollectorManager.getCollection(flow02Name).size());
        assertEquals(0, eventsCollectorManager.getCollection(flow03Name).size());
    }

    @Test
    void givenMultipleFlowsCollected_whenClearIsCalledForAFlow_thenOtherFlowsShouldNotBeAffected() {
        final String flow02Name = "flow02";
        final String flow03Name = "flow03";

        eventsCollectorManager.begin();
        eventsCollectorManager.begin(flow02Name);
        eventsCollectorManager.begin(flow03Name);

        eventsCollectorManager.clear();

        assertEquals(0, eventsCollectorManager.getCollection().size());
        assertEquals(1, eventsCollectorManager.getCollection(flow02Name).size());
        assertEquals(1, eventsCollectorManager.getCollection(flow03Name).size());

        eventsCollectorManager.clear(flow03Name);
        assertEquals(0, eventsCollectorManager.getCollection().size());
        assertEquals(1, eventsCollectorManager.getCollection(flow02Name).size());
        assertEquals(0, eventsCollectorManager.getCollection(flow03Name).size());

        eventsCollectorManager.clear(flow02Name);
        assertEquals(0, eventsCollectorManager.getCollection().size());
        assertEquals(0, eventsCollectorManager.getCollection(flow02Name).size());
        assertEquals(0, eventsCollectorManager.getCollection(flow03Name).size());
    }
}
