package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import mocks.ProceedingJointPointMockFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.InvalidParameterException;

import static mocks.ProceedingJointPointMockFactory.mockSubmitOnErrorFalse;
import static mocks.ProceedingJointPointMockFactory.mockSubmitOnErrorTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { BeginCollectingAspect.class })
public class BeginCollectingAspectTest {

    @Autowired
    private BeginCollectingAspect beginCollectingAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void beginCollectingSuccess() throws Throwable {
        final String expectedObject = "xpto";
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.proceed()).thenReturn(expectedObject);

        Object returnedObject = beginCollectingAspect.beginCollecting(proceedingJoinPoint);

        assertEquals(expectedObject, returnedObject);
        verify(eventsCollectorManager, times(1)).begin();
        verify(eventsCollectorManager, times(1)).submit();
    }

    @Test
    void beginCollectingErrorShouldCallSubmitWhenFlagIsTrue() throws Throwable {
        Class<InvalidParameterException> expectedException = InvalidParameterException.class;

        ProceedingJoinPoint proceedingJoinPoint = mockSubmitOnErrorTrue(expectedException);

        Throwable throwable = assertThrows(expectedException,
                () ->  beginCollectingAspect.beginCollecting(proceedingJoinPoint)
        );

        assertEquals(expectedException, throwable.getClass());
        verify(eventsCollectorManager, times(1)).begin();
        verify(eventsCollectorManager, times(1)).submit();
    }

    @Test
    void beginCollectingErrorShouldNotCallSubmitWhenFlagIsFalse() throws Throwable {
        Class<InvalidParameterException> expectedException = InvalidParameterException.class;

        ProceedingJoinPoint proceedingJoinPoint = mockSubmitOnErrorFalse(expectedException);

        Throwable throwable = assertThrows(expectedException,
                () ->  beginCollectingAspect.beginCollecting(proceedingJoinPoint)
        );

        assertEquals(expectedException, throwable.getClass());
        verify(eventsCollectorManager, times(1)).begin();
        verify(eventsCollectorManager, times(0)).submit();
        verify(eventsCollectorManager, times(1)).clear();
    }
}
