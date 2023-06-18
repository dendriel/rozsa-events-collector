package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.cached.ObjectCollectorManager;
import mocks.CollectObjectMock;
import mocks.ObjectCollectorsConfiguration;
import mocks.ObjectForCustomCollection;
import mocks.RecursiveCollectObjectMock;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static mocks.BeforeJoinPointMockFactory.*;
import static mocks.BeforeJoinPointMockScenarios.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { CollectAspect.class, ObjectCollectorsConfiguration.class, ObjectCollectorManager.class })
public class CollectAspectTest {
    @Autowired
    private CollectAspect collectAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @SpyBean
    private ObjectCollectorsConfiguration objectCollectorsConfiguration;

    @Test
    void givenNoParametersInJoinPoint_whenCollectIsCalled_thenNothingShouldHappen() throws NoSuchMethodException, IllegalAccessException {
        JoinPoint joinPoint = mockJoinPoint(MISSING_PARAMETERS);

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(0)).collect(any(), any());
    }

    @Test
    void givenNoCollectParametersInJoinPoint_whenCollectIsCalled_thenNothingShouldHappen() throws NoSuchMethodException, IllegalAccessException {
        JoinPoint joinPoint = mockJoinPoint(MISSING_COLLECT_PARAMETER);

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(0)).collect(any(), any());
    }

    @Test
    void givenSingleCollectParametersInJoinPoint_whenCollectIsCalled_thenAnnotatedParameterShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final String targetValue = "john doe";
        JoinPoint joinPoint = mockJoinPoint(SINGLE_COLLECT_PARAMETER, List.of(targetValue));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(1)).collect(eq(SINGLE_COLLECT_PARAMETER_DEFAULT_KEY), eq(targetValue));
    }

    @Test
    void givenSingleCollectParametersInJoinPointWithKey_whenCollectIsCalled_thenAnnotatedParameterShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final String targetValue = "john doe";
        JoinPoint joinPoint = mockJoinPoint(SINGLE_COLLECT_PARAMETER_CUSTOM, List.of(targetValue));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(1)).collect(eq(SINGLE_COLLECT_PARAMETER_CUSTOM_KEY), eq(targetValue));
    }

    @Test
    void givenSingleCollectParametersInJoinPointWithValueKey_whenCollectIsCalled_thenAnnotatedParameterShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final String targetValue = "john doe";
        JoinPoint joinPoint = mockJoinPoint(SINGLE_COLLECT_PARAMETER_CUSTOM_VALUE, List.of(targetValue));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(1)).collect(eq(SINGLE_COLLECT_PARAMETER_CUSTOM_VALUE_KEY), eq(targetValue));
    }

    @Test
    void givenMultiCollectParametersInJoinPoint_whenCollectIsCalled_thenOnlyAnnotatedParametersShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final String targetValue01 = "john doe";
        final Integer targetValue02 = 1999;
        JoinPoint joinPoint = mockJoinPoint(MULTI_COLLECT_PARAMETER, List.of(targetValue01, "dumb", targetValue02, "dummy"));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(2)).collect(any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(FIST_PARAMETER_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(THIRD_PARAMETER_KEY), eq(targetValue02));
    }

    @Test
    void givenScanFieldCollectParametersInJoinPoint_whenCollectIsCalled_thenAnnotatedFieldsShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = true;
        final Double targetValue02 = 31.99;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);
        JoinPoint joinPoint = mockJoinPoint(SCAN_FIELD_COLLECT_PARAMETER, List.of(captureObjectMock));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(2)).collect(any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
    }

    @Test
    void givenRecursiveScanFieldCollectParametersInJoinPoint_whenCollectIsCalled_thenAnnotatedFieldsShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = false;
        final Double targetValue02 = Double.MAX_VALUE;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);

        final int targetValue03 = 99887766;
        RecursiveCollectObjectMock recursiveCollectObjectMock = new RecursiveCollectObjectMock(captureObjectMock, targetValue03);

        JoinPoint joinPoint = mockJoinPoint(RECURSIVE_SCAN_FIELD_COLLECT_PARAMETER, List.of(recursiveCollectObjectMock));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(3)).collect(any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(RecursiveCollectObjectMock.COMPANION_FIELD_KEY), eq(targetValue03));
    }

    @Test
    void givenCustomObjectCollector_whenCollectIsCalled_thenCustomObjectCollectorShouldBeUsedToCollect() throws NoSuchMethodException, IllegalAccessException {
        final String name = "Thomas A. Anderson";
        final Integer age = 28;
        final List<String> hobbies = List.of("Entering the Matrix", "Fighting Agent Smith", "Hacking");
        ObjectForCustomCollection objectForCustomCollection = new ObjectForCustomCollection(name, age, hobbies);

        final String expectedValue = ObjectCollectorsConfiguration.formatCollectedValue(objectForCustomCollection);

        JoinPoint joinPoint = mockJoinPoint(CUSTOM_OBJECT_COLLECTION_PARAMETER, List.of(objectForCustomCollection));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(1)).collect(eq(
                ObjectCollectorsConfiguration.CUSTOM_OBJECT_COLLECTOR_KEY), eq(expectedValue));
    }

    @Test
    void coverPointCutEmptyMethod() {
        collectAspect.collectAnnotation();
    }
}
