package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import mocks.CollectObjectMock;
import mocks.CustomFlowCollectObjectMock;
import mocks.RecursiveCollectObjectMock;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static mocks.AfterJoinPointMockFactory.*;
import static mocks.AfterJoinPointMockScenarios.*;
import static mocks.CustomFlowCollectObjectMock.FIELD_OVERRIDE_CUSTOM_FLOW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { CollectReturnAspect.class })
public class CollectReturnAspectCustomFlowTest {

    @Autowired
    private CollectReturnAspect collectReturnAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void givenMethodMarkedForCollectionFlow_whenCollectReturnIsCalled_thenReturnValueShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final String returnedValue = "trinity";
        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_SIMPLE_RETURN);

        collectReturnAspect.collect(joinPoint, returnedValue);

        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(SINGLE_RETURN_CLASS_TYPE), eq(returnedValue));
    }

    @Test
    void givenMethodForCollectionHasScanFieldsFlow_whenCollectReturnIsCalled_thenMarkedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = false;
        final Double targetValue02 = 66.99;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);

        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_RETURN_WITH_SCAN_FIELDS);

        collectReturnAspect.collect(joinPoint, captureObjectMock);

        verify(eventsCollectorManager, times(2)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
    }

    @Test
    void givenMethodForCollectionHasRecursiveScanFields_whenCollectReturnIsCalled_thenAllMarkedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = true;
        final Double targetValue02 = Double.MIN_VALUE;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);

        final int targetValue03 = 54333221;
        RecursiveCollectObjectMock recursiveCollectObjectMock = new RecursiveCollectObjectMock(captureObjectMock, targetValue03);

        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_RETURN_WITH_RECURSIVE_SCAN_FIELDS);

        collectReturnAspect.collect(joinPoint, recursiveCollectObjectMock);

        verify(eventsCollectorManager, times(3)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(RecursiveCollectObjectMock.COMPANION_FIELD_KEY), eq(targetValue03));
    }

    @Test
    void givenCustomFlowFieldOverride_whenCollectReturnIsCalled_thenAnnotatedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = true;
        final Double targetValue02 = 31.99;
        final Integer targetValue03 = 99887766;
        CustomFlowCollectObjectMock captureObjectMock = new CustomFlowCollectObjectMock(targetValue01, targetValue02, targetValue03);
        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_RETURN_WITH_SCAN_FIELDS);

        collectReturnAspect.collect(joinPoint, captureObjectMock);

        verify(eventsCollectorManager, times(3)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(FIELD_OVERRIDE_CUSTOM_FLOW), eq(CustomFlowCollectObjectMock.FIELD_OVERRIDE_NAME), eq(targetValue03));
    }

    @Test
    void givenRecursiveCustomFlowFieldOverride_whenCollectReturnIsCalled_thenAnnotatedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = true;
        final Double targetValue02 = Double.MIN_VALUE;
        final Integer targetValue03 = 55443322;
        CustomFlowCollectObjectMock captureObjectMock = new CustomFlowCollectObjectMock(targetValue01, targetValue02, targetValue03);

        final int targetValue04 = 54333221;
        RecursiveCollectObjectMock recursiveCollectObjectMock = new RecursiveCollectObjectMock(captureObjectMock, targetValue04);

        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_RETURN_WITH_RECURSIVE_SCAN_FIELDS);

        collectReturnAspect.collect(joinPoint, recursiveCollectObjectMock);

        verify(eventsCollectorManager, times(4)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(AFTER_FIRST_CUSTOM_FLOW_NAME), eq(RecursiveCollectObjectMock.COMPANION_FIELD_KEY), eq(targetValue04));
        verify(eventsCollectorManager, times(1)).collect(eq(FIELD_OVERRIDE_CUSTOM_FLOW), eq(CustomFlowCollectObjectMock.FIELD_OVERRIDE_NAME), eq(targetValue03));
    }
}
