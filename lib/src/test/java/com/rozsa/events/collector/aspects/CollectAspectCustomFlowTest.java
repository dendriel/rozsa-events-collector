package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.cached.ObjectCollectorManager;
import mocks.*;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static mocks.BeforeJoinPointMockFactory.*;
import static mocks.BeforeJoinPointMockScenarios.*;
import static mocks.CustomFlowCollectObjectMock.FIELD_OVERRIDE_CUSTOM_FLOW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { CollectAspect.class, ObjectCollectorsConfiguration.class, ObjectCollectorManager.class })
public class CollectAspectCustomFlowTest {
    @Autowired
    private CollectAspect collectAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void givenSingleCollectParametersInJoinPointWithFlow_whenCollectIsCalled_thenAnnotatedParameterShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final String targetValue = "john doe";
        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_SINGLE_COLLECT_PARAMETER, List.of(targetValue));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(SINGLE_COLLECT_PARAMETER_DEFAULT_KEY), eq(targetValue));
    }

    @Test
    void givenMultiCollectParametersInJoinPointWithFlow_whenCollectIsCalled_thenOnlyAnnotatedParametersShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final String targetValue01 = "john doe";
        final Integer targetValue02 = 1999;
        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_MULTI_COLLECT_PARAMETER, List.of(targetValue01, "dumb", targetValue02, "dummy"));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(2)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(FIST_PARAMETER_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(THIRD_PARAMETER_KEY), eq(targetValue02));
    }

    @Test
    void givenScanFieldCollectParametersInJoinPointWithFlow_whenCollectIsCalled_thenAnnotatedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = true;
        final Double targetValue02 = 31.99;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);
        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_SCAN_FIELD_COLLECT_PARAMETER, List.of(captureObjectMock));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(2)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
    }

    @Test
    void givenRecursiveScanFieldCollectParametersInJoinPointWithFlow_whenCollectIsCalled_thenAnnotatedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = false;
        final Double targetValue02 = Double.MAX_VALUE;
        CollectObjectMock captureObjectMock = new CollectObjectMock(targetValue01, targetValue02);

        final int targetValue03 = 99887766;
        RecursiveCollectObjectMock recursiveCollectObjectMock = new RecursiveCollectObjectMock(captureObjectMock, targetValue03);

        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_RECURSIVE_SCAN_FIELD_COLLECT_PARAMETER, List.of(recursiveCollectObjectMock));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(3)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(RecursiveCollectObjectMock.COMPANION_FIELD_KEY), eq(targetValue03));
    }

    @Test
    void givenCustomObjectCollectorWithFlow_whenCollectIsCalled_thenCustomObjectCollectorShouldBeUsedToCollectWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final String name = "Thomas A. Anderson";
        final Integer age = 28;
        final List<String> hobbies = List.of("Entering the Matrix", "Fighting Agent Smith", "Hacking");
        ObjectForCustomCollection objectForCustomCollection = new ObjectForCustomCollection(name, age, hobbies);

        final String expectedValue = ObjectCollectorsConfiguration.formatCollectedValue(objectForCustomCollection);

        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_CUSTOM_OBJECT_COLLECTION_PARAMETER, List.of(objectForCustomCollection));

        collectAspect.collect(joinPoint);

        // Obviously, the forwarding of the flow name depends on the bean implementation.
        verify(eventsCollectorManager, times(1)).collect(
                eq(FIRST_CUSTOM_FLOW_NAME), eq(ObjectCollectorsConfiguration.CUSTOM_OBJECT_COLLECTOR_KEY), eq(expectedValue));
    }

    @Test
    void givenCustomFlowParamOverride_whenCollectIsCalled_thenCustomizedFlowValueShouldBeUsed() throws NoSuchMethodException, IllegalAccessException {
        final String targetValue01 = "john doe";
        final Integer targetValue02 = 1999;
        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_PARAM_OVERRIDE_MULTI_COLLECT_PARAMETER, List.of(targetValue01, "dumb", targetValue02, "dummy"));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(2)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(SECOND_CUSTOM_FLOW_NAME), eq(FIST_PARAMETER_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(THIRD_PARAMETER_KEY), eq(targetValue02));
    }

    @Test
    void givenCustomFlowFieldOverride_whenCollectIsCalled_thenAnnotatedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = true;
        final Double targetValue02 = 31.99;
        final Integer targetValue03 = 99887766;
        CustomFlowCollectObjectMock captureObjectMock = new CustomFlowCollectObjectMock(targetValue01, targetValue02, targetValue03);
        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_FIELD_OVERRIDE_SCAN_FIELD_COLLECT_PARAMETER, List.of(captureObjectMock));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(3)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(FIELD_OVERRIDE_CUSTOM_FLOW), eq(CustomFlowCollectObjectMock.FIELD_OVERRIDE_NAME), eq(targetValue03));
    }

    @Test
    void givenRecursiveCustomFlowFieldOverride_whenCollectIsCalled_thenAnnotatedFieldsShouldBeCollectedWithFlow() throws NoSuchMethodException, IllegalAccessException {
        final Boolean targetValue01 = false;
        final Double targetValue02 = Double.MAX_VALUE;
        final Integer targetValue03 = 55443322;
        CustomFlowCollectObjectMock captureObjectMock = new CustomFlowCollectObjectMock(targetValue01, targetValue02, targetValue03);

        final Integer targetValue04 = 1100;
        RecursiveCollectObjectMock recursiveCollectObjectMock = new RecursiveCollectObjectMock(captureObjectMock, targetValue04);

        JoinPoint joinPoint = mockJoinPoint(CUSTOM_FLOW_RECURSIVE_SCAN_FIELD_COLLECT_PARAMETER, List.of(recursiveCollectObjectMock));

        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(4)).collect(any(), any(), any());
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FINAL_FIELD_DEFAULT_KEY), eq(targetValue01));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(CollectObjectMock.FIELD_CUSTOM_KEY), eq(targetValue02));
        verify(eventsCollectorManager, times(1)).collect(eq(FIELD_OVERRIDE_CUSTOM_FLOW), eq(CustomFlowCollectObjectMock.FIELD_OVERRIDE_NAME), eq(targetValue03));
        verify(eventsCollectorManager, times(1)).collect(eq(FIRST_CUSTOM_FLOW_NAME), eq(RecursiveCollectObjectMock.COMPANION_FIELD_KEY), eq(targetValue04));
    }
}
