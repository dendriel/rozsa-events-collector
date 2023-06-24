package mocks;

import java.util.List;

public enum BeforeJoinPointMockScenarios {
    MISSING_PARAMETERS("missingParameters", List.of()),
    MISSING_COLLECT_PARAMETER("missingCollectParameterAnnotation",  List.of(String.class, Integer.class, Object.class)),
    SINGLE_COLLECT_PARAMETER("singleCollectParameterAnnotation", List.of(String.class)),
    SINGLE_COLLECT_PARAMETER_CUSTOM("singleCollectParameterCustomKeyAnnotation", List.of(String.class)),
    SINGLE_COLLECT_PARAMETER_CUSTOM_VALUE("singleCollectParameterCustomValueKeyAnnotation", List.of(String.class)),
    MULTI_COLLECT_PARAMETER("multipleCollectParameter",  List.of(String.class, String.class, Integer.class, String.class)),
    SCAN_FIELD_COLLECT_PARAMETER("scanFieldCollectParameter", List.of(CollectObjectMock.class)),
    RECURSIVE_SCAN_FIELD_COLLECT_PARAMETER("recursiveScanFieldCollectParameter", List.of(RecursiveCollectObjectMock.class)),
    CUSTOM_OBJECT_COLLECTION_PARAMETER("customObjectCollectionParameter", List.of(ObjectForCustomCollection.class)),
    CUSTOM_FLOW_SINGLE_COLLECT_PARAMETER("customFlowSingleCollectParameterAnnotation", List.of(String.class)),
    CUSTOM_FLOW_MULTI_COLLECT_PARAMETER("customFlowMultipleCollectParameter",  List.of(String.class, String.class, Integer.class, String.class)),
    CUSTOM_FLOW_SCAN_FIELD_COLLECT_PARAMETER("customFlowScanFieldCollectParameter", List.of(CollectObjectMock.class)),
    CUSTOM_FLOW_RECURSIVE_SCAN_FIELD_COLLECT_PARAMETER("customFlowRecursiveScanFieldCollectParameter", List.of(RecursiveCollectObjectMock.class)),
    CUSTOM_FLOW_CUSTOM_OBJECT_COLLECTION_PARAMETER("customFlowCustomObjectCollectionParameter", List.of(ObjectForCustomCollection.class)),
    CUSTOM_FLOW_PARAM_OVERRIDE_MULTI_COLLECT_PARAMETER("customFlowParamOverrideMultipleCollectParameter",  List.of(String.class, String.class, Integer.class, String.class)),
    CUSTOM_FLOW_FIELD_OVERRIDE_SCAN_FIELD_COLLECT_PARAMETER("customFlowFieldOverrideScanFieldCollectParameter", List.of(CustomFlowCollectObjectMock.class)),
    ;

    private final String method;
    private final List<Class> parameters;

    BeforeJoinPointMockScenarios(String method, List<Class> parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    public String getMethod() {
        return method;
    }

    public List<Class> getParameters() {
        return parameters;
    }
}
