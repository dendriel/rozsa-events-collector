package mocks;

import com.rozsa.events.collector.annotations.CollectReturn;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AfterJointPointMockFactory {

    public static final String SINGLE_RETURN_CLASS_TYPE = String.class.getName();

    public static final String SINGLE_RETURN_KEY_VALUE = "single_return_key";

    public static JoinPoint mockJoinPoint(final AfterJoinPointMockScenarios mockType) throws NoSuchMethodException {
        return mockJoinPoint(mockType, List.of());
    }

    public static JoinPoint mockJoinPoint(final AfterJoinPointMockScenarios mockType, final List<Object> args) throws NoSuchMethodException {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args.toArray());

        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = AfterJointPointMockFactory.class.getDeclaredMethod(mockType.getMethod(), mockType.getParameters().toArray(new Class[0]));

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        return joinPoint;
    }

    @CollectReturn
    public static String stringReturnCollectReturnAnnotation() { return ""; }

    @CollectReturn(SINGLE_RETURN_KEY_VALUE)
    public static String stringReturnWithKeyCollectReturnAnnotation() { return ""; }

    @CollectReturn(key = SINGLE_RETURN_KEY_VALUE)
    public static String stringReturnWithExplicitKeyCollectReturnAnnotation() { return ""; }

    @CollectReturn(scanFields = true)
    public static CollectObjectMock returnWithScanFieldsTrueCollectReturnAnnotation() { return null; }

    @CollectReturn(scanFields = true)
    public static RecursiveCollectObjectMock returnWithRecursiveScanFieldsTrueCollectReturnAnnotation() { return null; }
}
