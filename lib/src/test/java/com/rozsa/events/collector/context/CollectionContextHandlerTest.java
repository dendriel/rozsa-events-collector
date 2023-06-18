package com.rozsa.events.collector.context;

import com.rozsa.events.collector.context.api.CollectionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = { CollectionContextHandler.class })
public class CollectionContextHandlerTest {

    @Autowired
    private CollectionContextHandler collectionContextHandler;

    @BeforeEach
    void setup() {
        collectionContextHandler.clearAll();
    }

    @Test
    void givenHandlerHasNoFlows_whenInitializeIsCalled_thenANewFlowShouldBeCreated() {
        collectionContextHandler.initialize("new-flow-name");

        assertEquals(1, collectionContextHandler.count());
    }

    @Test
    void givenHandlerHasNoFlows_whenInitializeIsCalledMultipleTimes_thenShouldCreateAllFlows() {
        collectionContextHandler.initialize("new-flow-name01");
        collectionContextHandler.initialize("new-flow-name02");
        collectionContextHandler.initialize("new-flow-name03");

        assertEquals(3, collectionContextHandler.count());
    }

    @Test
    void givenFlowAlreadyCreated_whenInitializeIsCalledMultipleTimesForSameFlow_thenShouldNotHaveDuplicates() {
        collectionContextHandler.initialize("new-flow-name");
        collectionContextHandler.initialize("new-flow-name");
        collectionContextHandler.initialize("new-flow-name");

        assertEquals(1, collectionContextHandler.count());
    }

    @Test
    void givenFlowAlreadyCreated_whenGetIsCalled_thenShouldReturnFlow() {
        final String key = "testing-key";
        final String value = "xpto";
        final String flow = "specific-testable-flow";
        collectionContextHandler.initialize(flow);

        CollectionContext collectionContext = collectionContextHandler.get(flow);
        assertNotNull(collectionContext);

        collectionContext.add(key, value);

        collectionContext = collectionContextHandler.get(flow);
        assertEquals(1, collectionContext.getCollection().size());
        assertEquals(value, collectionContext.getCollection().get(key));
    }

    @Test
    void givenNonExistingFlow_whenGetIsCalled_thenShouldReturnNullFlow() {
        CollectionContext collectionContext = collectionContextHandler.get("dummy-key");
        assertNotNull(collectionContext);

        collectionContext.add("abc", 123);
        collectionContext.clear();

        Map<String, Object> collection = collectionContext.getCollection();
        assertNotNull(collection);
        assertEquals(0, collection.size());
    }

    @Test
    void givenHandlerHasSomeFlows_whenClearAllIsCalled_thenAllFlowsShouldBeRemoved() {
        collectionContextHandler.initialize("new-flow-name01");
        collectionContextHandler.initialize("new-flow-name02");
        collectionContextHandler.initialize("new-flow-name03");

        collectionContextHandler.clearAll();

        assertEquals(0, collectionContextHandler.count());
    }
}
