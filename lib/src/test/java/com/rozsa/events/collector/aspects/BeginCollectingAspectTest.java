package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.InvalidParameterException;

import static mocks.ProceedingJointPointMockFactory.*;
import static mocks.ProceedingJointPointMockScenarios.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { BeginCollectingAspect.class })
public class BeginCollectingAspectTest {

    @Autowired
    private BeginCollectingAspect beginCollectingAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void givenProceedWithReturnValue_whenTargetMethodReturns_thenValueShouldBeReturned() throws Throwable {
        final String expectedObject = "xpto";
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.proceed()).thenReturn(expectedObject);

        Object returnedObject = beginCollectingAspect.beginCollecting(proceedingJoinPoint);

        assertEquals(expectedObject, returnedObject);
        verify(eventsCollectorManager, times(1)).begin(StringUtils.EMPTY);
        verify(eventsCollectorManager, times(1)).submit(StringUtils.EMPTY);
    }

    @Test
    void givenProceedWithoutReturnValue_whenTargetMethodFinishes_thenNoValueShouldBeReturned() throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);

        Object returnedObject = beginCollectingAspect.beginCollecting(proceedingJoinPoint);

        assertNull(returnedObject);
        verify(eventsCollectorManager, times(1)).begin(StringUtils.EMPTY);
        verify(eventsCollectorManager, times(1)).submit(StringUtils.EMPTY);
    }

    @Test
    void givenSubmitOnErrorIsTrue_whenTargetMethodThrows_thenSubmitShouldBeCalled() throws Throwable {
        Class<InvalidParameterException> expectedException = InvalidParameterException.class;

        ProceedingJoinPoint proceedingJoinPoint = mockProceedingJoinPoint(SUBMIT_ON_ERROR_TRUE);
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);

        Throwable throwable = assertThrows(expectedException,
                () ->  beginCollectingAspect.beginCollecting(proceedingJoinPoint)
        );

        assertEquals(expectedException, throwable.getClass());
        verify(eventsCollectorManager, times(1)).begin(StringUtils.EMPTY);
        verify(eventsCollectorManager, times(1)).submit(StringUtils.EMPTY);
    }

    @Test
    void givenSubmitOnErrorIsFalse_whenTargetMethodThrows_thenSubmitShouldNotBeCalledAndClearShouldBeCalled() throws Throwable {
        Class<InvalidParameterException> expectedException = InvalidParameterException.class;

        ProceedingJoinPoint proceedingJoinPoint = mockProceedingJoinPoint(SUBMIT_ON_ERROR_FALSE);
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);


        Throwable throwable = assertThrows(expectedException,
                () ->  beginCollectingAspect.beginCollecting(proceedingJoinPoint)
        );

        assertEquals(expectedException, throwable.getClass());
        verify(eventsCollectorManager, times(1)).begin(StringUtils.EMPTY);
        verify(eventsCollectorManager, times(0)).submit(any());
        verify(eventsCollectorManager, times(1)).clear(StringUtils.EMPTY);
    }

    @Test
    void givenCustomFlowSet_whenTargetMethodRuns_thenCustomFlowShouldBeUsed() throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mockProceedingJoinPoint(CUSTOM_FLOW);

        beginCollectingAspect.beginCollecting(proceedingJoinPoint);

        verify(eventsCollectorManager, times(1)).begin(CUSTOM_FLOW_NAME);
        verify(eventsCollectorManager, times(1)).submit(CUSTOM_FLOW_NAME);
    }

    @Test
    void givenCustomFlowSetAndSubmitOnErrorFalse_whenTargetMethodThrows_thenCustomFlowShouldBeUsedInClear() throws Throwable {
        Class<InvalidParameterException> expectedException = InvalidParameterException.class;

        ProceedingJoinPoint proceedingJoinPoint = mockProceedingJoinPoint(CUSTOM_FLOW_SUBMIT_ON_ERROR_DISABLED);
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);

        Throwable throwable = assertThrows(expectedException,
                () ->  beginCollectingAspect.beginCollecting(proceedingJoinPoint)
        );

        assertEquals(expectedException, throwable.getClass());
        verify(eventsCollectorManager, times(1)).begin(CUSTOM_FLOW_NAME);
        verify(eventsCollectorManager, times(1)).clear(CUSTOM_FLOW_NAME);
    }

    @Test
    void coverPointCutEmptyMethod() {
        beginCollectingAspect.beginCollectingAnnotation();
    }
}
