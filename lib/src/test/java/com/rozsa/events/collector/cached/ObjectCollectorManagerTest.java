package com.rozsa.events.collector.cached;

import com.rozsa.events.collector.api.ObjectCollector;
import mocks.AnotherObjectCollectorsConfiguration;
import mocks.ObjectCollectorsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static mocks.AnotherObjectCollectorsConfiguration.ANOTHER_CUSTOM_OBJECT_COLLECTOR;
import static mocks.AnotherObjectCollectorsConfiguration.ANOTHER_CUSTOM_OBJECT_COLLECTOR_NAME;
import static mocks.ObjectCollectorsConfiguration.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { ObjectCollectorManager.class, ObjectCollectorsConfiguration.class, AnotherObjectCollectorsConfiguration.class })
public class ObjectCollectorManagerTest {
    private static final String DEFAULT_COLLECTOR_NAME = "^com.rozsa.events.collector.cached.ObjectCollectorManager\\$\\$Lambda\\$\\S+$";
    private static final String TEST_COLLECTOR_NAME = "^com.rozsa.events.collector.cached.ObjectCollectorManagerTest\\$\\$Lambda\\$\\S+$";

    @Autowired
    private ObjectCollectorManager objectCollectorManager;

    @MockBean
    private ApplicationContext applicationContext;

    @Test
    void givenNonexistentBeanNameUsed_whenGetBean_thenDefaultCollectorShouldBeReturned() {
        ObjectCollector result = objectCollectorManager.getBean("nonexistent-collector-bean");
        assertNotNull(result);

        assertLinesMatch(List.of(DEFAULT_COLLECTOR_NAME), List.of(result.getClass().getName()));
    }

    @Test
    void givenInvalidObjectCollectorBeanRequested_whenGetBean_thenDefaultCollectorShouldBeReturned() {
        ObjectCollector result = objectCollectorManager.getBean(INVALID_CUSTOM_OBJECT_COLLECTOR);
        assertNotNull(result);

        assertLinesMatch(List.of(DEFAULT_COLLECTOR_NAME), List.of(result.getClass().getName()));
    }

    @Test
    void givenBrokenObjectCollectorBeanRequested_whenGetBean_thenDefaultCollectorShouldBeReturned() {
        ObjectCollector result = objectCollectorManager.getBean(BROKEN_CUSTOM_OBJECT_COLLECTOR);
        assertNotNull(result);

        assertLinesMatch(List.of(DEFAULT_COLLECTOR_NAME), List.of(result.getClass().getName()));
    }

    @Test
    void givenValidBeanNameUsed_whenGetBean_thenExpectedBeanShouldBeReturn() {
        ObjectCollector result = objectCollectorManager.getBean(CUSTOM_OBJECT_COLLECTOR);
        assertNotNull(result);

        assertLinesMatch(List.of(CUSTOM_OBJECT_COLLECTOR_NAME), List.of(result.getClass().getName()));
    }

    @Test
    void givenCacheIsNotEmpty_whenGetBean_thenCachedBeanShouldReturned() {
        final String testCollectorBean = "test-collector";

        when(applicationContext.getBean(testCollectorBean, ObjectCollector.class))
                .thenReturn((f, v, m) -> {});
        objectCollectorManager = new ObjectCollectorManager(applicationContext);

        // first call to cache the bean.
        ObjectCollector result = objectCollectorManager.getBean(testCollectorBean);
        assertNotNull(result);

        // second call should retrieve bean from cache.
        result = objectCollectorManager.getBean(testCollectorBean);
        assertNotNull(result);

        assertLinesMatch(List.of(TEST_COLLECTOR_NAME), List.of(result.getClass().getName()));
        verify(applicationContext, times(1)).getBean(eq(testCollectorBean), eq(ObjectCollector.class));
    }

    @Test
    void givenCacheHasManyBeans_whenGetBean_thenCorrectCachedBeanShouldReturned() {
        // cache beans.
        objectCollectorManager.getBean(CUSTOM_OBJECT_COLLECTOR);
        objectCollectorManager.getBean(ANOTHER_CUSTOM_OBJECT_COLLECTOR);

        // fetch beans.
        ObjectCollector result =  objectCollectorManager.getBean(CUSTOM_OBJECT_COLLECTOR);
        assertLinesMatch(List.of(CUSTOM_OBJECT_COLLECTOR_NAME), List.of(result.getClass().getName()));

        result =  objectCollectorManager.getBean(ANOTHER_CUSTOM_OBJECT_COLLECTOR);
        assertLinesMatch(List.of(ANOTHER_CUSTOM_OBJECT_COLLECTOR_NAME), List.of(result.getClass().getName()));
    }

    @Test
    void coverEmptyCollector() {
        ObjectCollector emptyCollector = objectCollectorManager.getBean("nonexisting-bean");
        emptyCollector.collect(null, null, null);
    }
}

