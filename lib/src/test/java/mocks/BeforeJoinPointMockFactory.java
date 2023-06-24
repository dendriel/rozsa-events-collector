package mocks;

import com.rozsa.events.collector.annotations.Collect;
import com.rozsa.events.collector.annotations.CollectParameter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BeforeJoinPointMockFactory {
    public static final String FIRST_CUSTOM_FLOW_NAME = "customFlow01";
    public static final String SECOND_CUSTOM_FLOW_NAME = "customFlow02";
    public static final String SINGLE_COLLECT_PARAMETER_DEFAULT_KEY = "parameterName";
    public static final String SINGLE_COLLECT_PARAMETER_CUSTOM_KEY = "single_collect_param_key";
    public static final String SINGLE_COLLECT_PARAMETER_CUSTOM_VALUE_KEY = "single_collect_param_value_key";
    public static final String FIST_PARAMETER_KEY = "parameterName01";
    public static final String SECOND_PARAMETER_KEY = "parameterName02";
    public static final String THIRD_PARAMETER_KEY = "custom_key_for_parameterName03";
    public static final String FOURTH_PARAMETER_KEY = "parameterName04";

    public static JoinPoint mockJoinPoint(final BeforeJoinPointMockScenarios scenario) throws NoSuchMethodException {
        return mockJoinPoint(scenario, List.of());
    }

    public static JoinPoint mockJoinPoint(final BeforeJoinPointMockScenarios scenario, final List<Object> args) throws NoSuchMethodException {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args.toArray());

        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = BeforeJoinPointMockFactory.class.getDeclaredMethod(scenario.getMethod(), scenario.getParameters().toArray(new Class[0]));

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        return joinPoint;
    }

    public static void singleCollectParameterAnnotation(@CollectParameter final String parameterName) {}

    @Collect(flow = FIRST_CUSTOM_FLOW_NAME)
    public static void customFlowSingleCollectParameterAnnotation(@CollectParameter final String parameterName) {}

    public static void singleCollectParameterCustomKeyAnnotation(
            @CollectParameter(key = SINGLE_COLLECT_PARAMETER_CUSTOM_KEY) final String parameterName
    ) {}

    public static void singleCollectParameterCustomValueKeyAnnotation(
            @CollectParameter(SINGLE_COLLECT_PARAMETER_CUSTOM_VALUE_KEY) final String parameterName
    ) {}

    public static void missingParameters() {}

    public static void missingCollectParameterAnnotation(String foo, Integer bar, Object xpto) {}

    public static void multipleCollectParameter(
            @CollectParameter final String parameterName01,
            final String parameterName02,
            @CollectParameter(THIRD_PARAMETER_KEY) Integer parameterName03,
            final String parameterName04
    ) {}

    @Collect(flow = FIRST_CUSTOM_FLOW_NAME)
    public static void customFlowMultipleCollectParameter(
            @CollectParameter final String parameterName01,
            final String parameterName02,
            @CollectParameter(THIRD_PARAMETER_KEY) Integer parameterName03,
            final String parameterName04
    ) {}

    @Collect(flow = FIRST_CUSTOM_FLOW_NAME)
    public static void customFlowParamOverrideMultipleCollectParameter(
            @CollectParameter(flow = SECOND_CUSTOM_FLOW_NAME) final String parameterName01,
            final String parameterName02,
            @CollectParameter(THIRD_PARAMETER_KEY) Integer parameterName03,
            final String parameterName04
    ) {}

    public static void scanFieldCollectParameter(
            @CollectParameter(scanFields = true) CollectObjectMock parameterName01
    ) {}

    @Collect(flow = FIRST_CUSTOM_FLOW_NAME)
    public static void customFlowScanFieldCollectParameter(
            @CollectParameter(scanFields = true) CollectObjectMock parameterName01
    ) {}

    @Collect(flow = FIRST_CUSTOM_FLOW_NAME)
    public static void customFlowFieldOverrideScanFieldCollectParameter(
            @CollectParameter(scanFields = true) CustomFlowCollectObjectMock parameterName01
    ) {}

    public static void recursiveScanFieldCollectParameter(
            @CollectParameter(scanFields = true) RecursiveCollectObjectMock parameterName01
    ) {}

    @Collect(flow = FIRST_CUSTOM_FLOW_NAME)
    public static void customFlowRecursiveScanFieldCollectParameter(
            @CollectParameter(scanFields = true) RecursiveCollectObjectMock parameterName01
    ) {}

    public static void customObjectCollectionParameter(
            @CollectParameter(collector = ObjectCollectorsConfiguration.CUSTOM_OBJECT_COLLECTOR) ObjectForCustomCollection parameterName01
    ) {}

    @Collect(flow = FIRST_CUSTOM_FLOW_NAME)
    public static void customFlowCustomObjectCollectionParameter(
            @CollectParameter(collector = ObjectCollectorsConfiguration.CUSTOM_OBJECT_COLLECTOR) ObjectForCustomCollection parameterName01
    ) {}
}
