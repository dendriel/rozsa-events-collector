package mocks;

import com.rozsa.events.collector.annotations.CollectReturn;
import com.rozsa.events.collector.annotations.FinishCollecting;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AfterJoinPointMockFactory {
    public static final String AFTER_FIRST_CUSTOM_FLOW_NAME = "customFlow01";
    public static final String SINGLE_RETURN_CLASS_TYPE = String.class.getName();
    public static final String SINGLE_RETURN_KEY_VALUE = "single_return_key";

    public static JoinPoint mockJoinPoint(final AfterJoinPointMockScenarios scenario) throws NoSuchMethodException {
        return mockJoinPoint(scenario, List.of());
    }

    public static JoinPoint mockJoinPoint(final AfterJoinPointMockScenarios scenario, final List<Object> args) throws NoSuchMethodException {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args.toArray());

        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = AfterJoinPointMockFactory.class.getDeclaredMethod(scenario.getMethod(), scenario.getParameters().toArray(new Class[0]));

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        return joinPoint;
    }

    @CollectReturn
    public static String stringReturnCollectReturnAnnotation() { return ""; }

    @CollectReturn(flow = AFTER_FIRST_CUSTOM_FLOW_NAME)
    public static String customFlowStringReturnCollectReturnAnnotation() { return ""; }

    @CollectReturn(SINGLE_RETURN_KEY_VALUE)
    public static String stringReturnWithKeyCollectReturnAnnotation() { return ""; }

    @CollectReturn(key = SINGLE_RETURN_KEY_VALUE)
    public static String stringReturnWithExplicitKeyCollectReturnAnnotation() { return ""; }

    @CollectReturn(scanFields = true)
    public static CollectObjectMock returnWithScanFieldsTrueCollectReturnAnnotation() { return null; }

    @CollectReturn(flow = AFTER_FIRST_CUSTOM_FLOW_NAME, scanFields = true)
    public static CollectObjectMock customFlowReturnWithScanFieldsTrueCollectReturnAnnotation() { return null; }

    @CollectReturn(scanFields = true)
    public static RecursiveCollectObjectMock returnWithRecursiveScanFieldsTrueCollectReturnAnnotation() { return null; }

    @CollectReturn(flow = AFTER_FIRST_CUSTOM_FLOW_NAME, scanFields = true)
    public static RecursiveCollectObjectMock customFlowReturnWithRecursiveScanFieldsTrueCollectReturnAnnotation() { return null; }

    @CollectReturn(collector = ObjectCollectorsConfiguration.CUSTOM_OBJECT_COLLECTOR)
    public static void customReturnObjectCollection() {}

    @CollectReturn(flow = AFTER_FIRST_CUSTOM_FLOW_NAME, collector = ObjectCollectorsConfiguration.CUSTOM_OBJECT_COLLECTOR)
    public static void customFlowCustomReturnObjectCollection() {}

    @FinishCollecting
    public static String finishCollectionAnnotation() { return ""; }

    @FinishCollecting(flow = AFTER_FIRST_CUSTOM_FLOW_NAME)
    public static String customFlowFinishCollectionAnnotation() { return ""; }
}
