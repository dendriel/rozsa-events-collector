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
