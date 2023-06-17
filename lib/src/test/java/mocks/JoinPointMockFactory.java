package mocks;

import com.rozsa.events.collector.annotations.CollectParameter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinPointMockFactory {
    public static final String SINGLE_COLLECT_PARAMETER_DEFAULT_KEY = "parameterName";
    public static final String SINGLE_COLLECT_PARAMETER_CUSTOM_KEY = "single_collect_param_key";
    public static final String FIST_PARAMETER_KEY = "parameterName01";
    public static final String SECOND_PARAMETER_KEY = "parameterName02";
    public static final String THIRD_PARAMETER_KEY = "parameterName03";
    public static final String FOURTH_PARAMETER_KEY = "parameterName04";

    public static JoinPoint mockJoinPoint(final JoinPointMockTypes mockType) throws NoSuchMethodException {
        return mockJoinPoint(mockType, List.of());
    }

    public static JoinPoint mockJoinPoint(final JoinPointMockTypes mockType, final List<Object> args) throws NoSuchMethodException {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args.toArray());

        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = JoinPointMockFactory.class.getDeclaredMethod(mockType.getMethod(), mockType.getParameters().toArray(new Class[0]));

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        return joinPoint;
    }

    public static void singleCollectParameterAnnotation(@CollectParameter final String parameterName) {}

    public static void singleCollectParameterCustomKeyAnnotation(
            @CollectParameter(key = SINGLE_COLLECT_PARAMETER_CUSTOM_KEY) final String parameterName
    ) {}

    public static void missingParameters() {}

    public static void missingCollectParameterAnnotation(String foo, Integer bar, Object xpto) {}

    public static void multipleCollectParameter(
            @CollectParameter final String parameterName01,
            final String parameterName02,
            @CollectParameter Integer parameterName03,
            final String parameterName04
    ) {}

    public static void scanFieldCollectParameter(
            @CollectParameter(scanFields = true) CollectObjectMock parameterName01
    ) {}

    public static void recursiveScanFieldCollectParameter(
            @CollectParameter(scanFields = true) RecursiveCollectObjectMock parameterName01
    ) {}
}
