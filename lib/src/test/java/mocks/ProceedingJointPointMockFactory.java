package mocks;


import com.rozsa.events.collector.annotations.BeginCollecting;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProceedingJointPointMockFactory {
    public static final String CUSTOM_FLOW_NAME = "Awesome-Custom-Flow-Name";

    public static ProceedingJoinPoint mockProceedingJoinPoint(final ProceedingJointPointMockScenarios scenario) throws NoSuchMethodException {
        return mockProceedingJoinPoint(scenario, List.of());
    }

    public static ProceedingJoinPoint mockProceedingJoinPoint(final ProceedingJointPointMockScenarios scenario, final List<Object> args) throws NoSuchMethodException {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args.toArray());

        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = ProceedingJointPointMockFactory.class.getDeclaredMethod(scenario.getMethod());

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        return joinPoint;
    }
    @BeginCollecting
    public void annotatedBeginCollectingSubmitOnErrorTrue() {}

    @BeginCollecting(submitOnError = false)
    public void annotatedBeginCollectingSubmitOnErrorFalse() {}

    @BeginCollecting(flow = CUSTOM_FLOW_NAME)
    public void annotatedBeginCollectingCustomFlow() {}

    @BeginCollecting(flow = CUSTOM_FLOW_NAME, submitOnError = false)
    public void annotatedBeginCollectingCustomFlowSubmitOnErrorFalse() {}
}
