package mocks;


import com.rozsa.events.collector.annotations.BeginCollecting;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProceedingJointPointMockFactory {

    public static <T extends Throwable> ProceedingJoinPoint mockSubmitOnErrorTrue(final Class<T> exception)
            throws Throwable {
        return mockSubmitOnError(exception, true);
    }

    public static <T extends Throwable> ProceedingJoinPoint mockSubmitOnErrorFalse(final Class<T> exception)
            throws Throwable {
        return mockSubmitOnError(exception, false);
    }

    public static <T extends Throwable> ProceedingJoinPoint mockSubmitOnError(
            final Class<T> exception,
            final Boolean isSubmitOnError
    ) throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.proceed()).thenThrow(exception);

        MethodSignature methodSignature = mock(MethodSignature.class);

        if (isSubmitOnError) {
            when(methodSignature.getMethod()).thenReturn(beginCollectingSubmitOnErrorTrue());
        } else {
            when(methodSignature.getMethod()).thenReturn(beginCollectingSubmitOnErrorFalse());
        }

        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);

        return proceedingJoinPoint;
    }

    public static Method beginCollectingSubmitOnErrorTrue() throws NoSuchMethodException {
        return ProceedingJointPointMockFactory.class.getDeclaredMethod("annotatedBeginCollectingSubmitOnErrorTrue");
    }

    @BeginCollecting(submitOnError = true)
    public void annotatedBeginCollectingSubmitOnErrorTrue() {}


    public static Method beginCollectingSubmitOnErrorFalse() throws NoSuchMethodException {
        return ProceedingJointPointMockFactory.class.getDeclaredMethod("annotatedBeginCollectingSubmitOnErrorFalse");
    }

    @BeginCollecting(submitOnError = false)
    public void annotatedBeginCollectingSubmitOnErrorFalse() {}
}
