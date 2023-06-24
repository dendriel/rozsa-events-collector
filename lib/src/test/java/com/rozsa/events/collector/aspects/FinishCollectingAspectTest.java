package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import mocks.AfterJoinPointMockScenarios;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static mocks.AfterJoinPointMockFactory.AFTER_FIRST_CUSTOM_FLOW_NAME;
import static mocks.AfterJoinPointMockFactory.mockJoinPoint;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = { FinishCollectingAspect.class })
public class FinishCollectingAspectTest {

    @Autowired
    private FinishCollectingAspect finishCollectingAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void whenFinishCollectingIsCalled_thenSubmitShouldBeCalled() throws IOException, NoSuchMethodException {
        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.FINISH_COLLECTING);
        finishCollectingAspect.finishCollecting(joinPoint);

        verify(eventsCollectorManager, times(1)).submit(eq(StringUtils.EMPTY));
    }

    @Test
    void whenFinishCollectingIsCalledFlow_thenSubmitShouldBeCalledWithFlow() throws IOException, NoSuchMethodException {
        JoinPoint joinPoint = mockJoinPoint(AfterJoinPointMockScenarios.CUSTOM_FLOW_FINISH_COLLECTING);
        finishCollectingAspect.finishCollecting(joinPoint);

        verify(eventsCollectorManager, times(1)).submit(eq(AFTER_FIRST_CUSTOM_FLOW_NAME));
    }

    @Test
    void coverPointCutEmptyMethod() {
        finishCollectingAspect.finishCollectingAnnotation();
    }
}
