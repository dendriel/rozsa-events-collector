package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import mocks.JoinPointMockFactory;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static mocks.JoinPointMockFactory.JoinPointMockTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { CollectAspect.class })
public class CollectAspectTest {
    @Autowired
    private CollectAspect collectAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void givenNoParametersInJoinPoint_whenCollectIsCalled_thenNothingShouldHappen() throws NoSuchMethodException, IllegalAccessException {
        JoinPoint joinPoint = JoinPointMockFactory.mockJoinPoint(MISSING_PARAMETERS);
        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(0)).collect(any(), any());
    }

    @Test
    void givenNoCollectParametersInJoinPoint_whenCollectIsCalled_thenNothingShouldHappen() throws NoSuchMethodException, IllegalAccessException {
        JoinPoint joinPoint = JoinPointMockFactory.mockJoinPoint(MISSING_COLLECT_PARAMETER);
        collectAspect.collect(joinPoint);

        verify(eventsCollectorManager, times(0)).collect(any(), any());
    }
}
