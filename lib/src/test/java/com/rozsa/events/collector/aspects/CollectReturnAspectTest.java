package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import mocks.AfterJoinPointMockScenarios;
import mocks.CollectObjectMock;
import mocks.RecursiveCollectObjectMock;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static mocks.AfterJointPointMockFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { CollectReturnAspect.class })
public class CollectReturnAspectTest {

    @Autowired
    private CollectReturnAspect collectReturnAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void givenMethodMarkedForCollection_whenCollectReturnIsCalled_thenReturnValueShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final String returnedValue = "trinity";
        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.SIMPLE_RETURN);

        collectReturnAspect.collect(joinPoint, returnedValue);

        verify(eventsCollectorManager, times(1)).collect(eq(SINGLE_RETURN_CLASS_TYPE), eq(returnedValue));
    }

    @Test
    void givenMethodForCollectionHasNoKeyAndReturnsNullValue_whenCollectReturnIsCalled_thenNothingShouldBeCaptured() throws NoSuchMethodException, IllegalAccessException {
        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.SIMPLE_RETURN);

        collectReturnAspect.collect(joinPoint, null);

        verify(eventsCollectorManager, times(0)).collect(any(), any());
    }

    @Test
    void givenMethodForCollectionHasKeyAndReturnsNullValue_whenCollectReturnIsCalled_thenNullShouldBeCaptured() throws NoSuchMethodException, IllegalAccessException {
        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.RETURN_WITH_KEY);

        collectReturnAspect.collect(joinPoint, null);

        verify(eventsCollectorManager, times(1)).collect(eq(SINGLE_RETURN_KEY_VALUE), eq(null));
    }

    @Test
    void givenMethodForCollectionHasExplicitKeyAndReturnsNullValue_whenCollectReturnIsCalled_thenNullShouldBeCaptured() throws NoSuchMethodException, IllegalAccessException {
        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.RETURN_WITH_EXPLICIT_KEY);

        collectReturnAspect.collect(joinPoint, null);

        verify(eventsCollectorManager, times(1)).collect(eq(SINGLE_RETURN_KEY_VALUE), eq(null));
    }

    @Test
    void givenMethodForCollectionHasScanFields_whenCollectReturnIsCalled_thenMarkedFieldsShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = false;
        final Double targetValue02 = 66.99;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);

        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.RETURN_WITH_SCAN_FIELDS);

        collectReturnAspect.collect(joinPoint, captureObjectMock);

        verify(eventsCollectorManager, times(2)).collect(any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
    }

    @Test
    void givenMethodForCollectionHasRecursiveScanFields_whenCollectReturnIsCalled_thenAllMarkedFieldsShouldBeCollected() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = true;
        final Double targetValue02 = Double.MIN_VALUE;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);

        final int targetValue03 = 54333221;
        RecursiveCollectObjectMock recursiveCollectObjectMock = new RecursiveCollectObjectMock(captureObjectMock, targetValue03);

        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.RETURN_WITH_RECURSIVE_SCAN_FIELDS);

        collectReturnAspect.collect(joinPoint, recursiveCollectObjectMock);

        verify(eventsCollectorManager, times(3)).collect(any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(RecursiveCollectObjectMock.COMPANION_FIELD_KEY), eq(targetValue03));
    }

    @Test
    void coverPointCutEmptyMethod() {
        collectReturnAspect.collectReturnAnnotation();
    }
}
