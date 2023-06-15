package com.rozsa.events.collector.aspects;

import com.rozsa.events.collector.EventsCollectorManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = { FinishCollectingAspect.class })
public class FinishCollectingAspectTest {

    @Autowired
    private FinishCollectingAspect finishCollectingAspect;

    @MockBean
    private EventsCollectorManager eventsCollectorManager;

    @Test
    void finishCollectingSuccess() throws IOException {
        finishCollectingAspect.finishCollecting();

        verify(eventsCollectorManager, times(1)).submit();
    }
}
